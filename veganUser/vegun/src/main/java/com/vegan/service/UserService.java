package com.vegan.service;

import com.vegan.entity.User;
import com.vegan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepo;


    // 로그인 처리
    public Optional<User> login(String username, String password) {
        return userRepo.findByUsernameAndPassword(username, password); //사용자 이름과 비밀번호로 DB 조회
    }

    // 관리자 여부 확인
    public boolean isAdmin(User user) {
        return "ADMIN".equals(user.getRole()); //User 객체의 role이 "ADMIN"인지 확인 //관리자인 경우 true, 일반 사용자면 false 반환
    }
    //회원가입 처리
    public int join(User user) {
        System.out.println("==> user joined");
        validateDuplicateUser(user);  // 중복 아이디 확인
        userRepo.save(user);           // DB 저장
        return user.getId();           // 새로 생성된 회원 ID 반환
    }
    //중복 아이디 검증
    private void validateDuplicateUser(User user) {
        userRepo.findByUsername(user.getUsername()) //기존 사용자 조회
                .ifPresent(m ->{
                    throw new IllegalStateException("이미 존재하는 아이디입니다.");
                });

    }
    // 회원 탈퇴 처리
    public void deleteUser(int userId) {
        // 1. 회원 존재 여부 확인
        Optional<User> userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalStateException("존재하지 않는 회원입니다.");
        }

        // 2. 회원 삭제
        userRepo.delete(userId);
    }
    //회원 정보 수정
    public User update(User updatedUser) {
        // 1. 기존 회원 조회

        User existingUser = userRepo.findById(updatedUser.getId())
                .orElseThrow(() -> new IllegalStateException("회원이 존재하지 않습니다."));

        // 2. 변경 정보 적용
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setNickname(updatedUser.getNickname());
        existingUser.setPassword(updatedUser.getPassword());
        existingUser.setBirthdate(updatedUser.getBirthdate());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setEmail(updatedUser.getEmail());

        // 3. DB 업데이트
        userRepo.update(existingUser);
        return existingUser;
    }



    //ID로 회원 조회
    public Optional<User> findById(int userId) {
        return userRepo.findById(userId);
    }
    public void changePassword(int userId, String nowPw, String newPw) {
        // 1. 기존 회원 조회
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalStateException("회원이 존재하지 않습니다."));

        // 2. 현재 비밀번호 검증
        if (!user.getPassword().equals(nowPw)) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 3. 새 비밀번호 적용
        user.setPassword(newPw);

        // 4. DB 업데이트
        userRepo.update(user);
    }



    // 모든 사용자 가져오기
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    // id로 사용자 조회
    public Optional<User> getUserById(int id) {
        return userRepo.findById(id);
    }

    // 회원 추가
    public void addUser(User user) {
        userRepo.save(user);
    }


}



