package com.vegan.service;

import com.vegan.entity.User;
import com.vegan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;

    //로그인 사용자 이름으로
    public Optional<User> getUserByLoginId(String username) {
        return userRepo.findByUsername(username);
    }
    // username으로 사용자 조회
    public Optional<User> findByUsername(String username){
        return userRepo.findByUsername(username);
    }

    // 사용자 삭제 (username 기준)
    public void deleteUser(String username){
        userRepo.deleteByUsername(username);
    }

    //회원 수정
    public void updateUser(User user) {
        // username 중복 체크
        Optional<User> existing = userRepo.findByUsername(user.getUsername());
        if (existing.isPresent() && existing.get().getId() != user.getId()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        userRepo.updateUser(user);
    }
    // 비밀번호 변경
    public void updatePassword(int userId, String newPassword) {
        int result = userRepo.updatePassword(userId, newPassword);
        if (result == 0) {
            throw new IllegalArgumentException("비밀번호 변경 실패: 사용자 없음");
        }
    }
    // id로 사용자 조회
    public User findById(int id) {
        return userRepo.findById(id);
    }
    // 회원가입
    public void addUser(User user) {
        // 아이디 중복 확인
        Optional<User> existingUser = userRepo.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // DB 저장
        userRepo.save(user);
    }

}

