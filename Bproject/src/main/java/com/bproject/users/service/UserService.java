package com.bproject.users.service;

import com.bproject.users.entity.User;
import com.bproject.users.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** 아이디(=username) 중복 확인 */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    /** 회원가입 */
    public long register(Map<String, Object> req) {
        // 요청 맵에서 값 추출 (프런트가 보내는 키와 맞춤)
        String name     = (String) req.get("name");
        String username = (String) req.get("username");
        String nickname = (String) req.get("nickname");
        String password = (String) req.get("password");
        String birth    = (String) req.get("birth");     // yyyy-MM-dd
        String phone    = (String) req.get("phone");
        String email    = (String) req.get("email");

        if (name == null || username == null || nickname == null || password == null
                || birth == null || phone == null || email == null) {
            throw new IllegalArgumentException("필수 값 누락");
        }

        if (!isUsernameAvailable(username)) {
            throw new IllegalStateException("이미 사용 중인 아이디입니다.");
        }

        // 비밀번호 해시
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());

        User user = User.builder()
                .name(name)
                .username(username)
                .nickname(nickname)
                .password(hashed)
                .birthdate(LocalDate.parse(birth))
                .phone(phone)
                .email(email)
                .role("USER")
                .build();

        return userRepository.save(user);
    }

    /** 로그인 인증 */
    public User authenticate(String username, String rawPassword) {
        Optional<User> opt = userRepository.findByUsername(username);
        if (opt.isEmpty()) return null;

        User user = opt.get();
        boolean ok = BCrypt.checkpw(rawPassword, user.getPassword());
        return ok ? user : null;
    }
}
