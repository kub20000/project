package com.vegan.service;

import com.vegan.entity.Role;
import com.vegan.entity.User;
import com.vegan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepo;

     //로그인
    public Optional<User> login(String username, String password) {
        Optional<User> userOpt = userRepo.findByUsernameAndPassword(username, password);
        return userOpt;
    }
    // 회원가입
    // -----------------------
    public void join(User user) {
        // 아이디/닉네임 중복 체크
        validateDuplicateUser(user);
        // DB 저장
        userRepo.save(user);
    }

    // 아이디 존재 여부 확인
    // -----------------------
    public boolean existsByUsername(String username) {
        return userRepo.findByUsername(username).isPresent();
    }

    // -----------------------
    // 닉네임 존재 여부 확인
    public boolean existsByNickname(String nickname) {
        return userRepo.findByNickname(nickname).isPresent();
    }


    // 중복 검증
    private void validateDuplicateUser(User user) {
        // 아이디 중복
        userRepo.findByUsername(user.getUsername())
                .ifPresent(u -> { throw new IllegalStateException("이미 존재하는 아이디입니다."); });

        // 닉네임 중복
        userRepo.findByNickname(user.getNickname())
                .ifPresent(u -> { throw new IllegalStateException("이미 존재하는 닉네임입니다."); });
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
        existingUser.setName(updatedUser.getName());
        existingUser.setNickname(updatedUser.getNickname());
        existingUser.setPassword(updatedUser.getPassword());
        existingUser.setBirthdate(updatedUser.getBirthdate());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setRole(updatedUser.getRole());

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
    //나의 냉장고
    @GetMapping("/myFridge")
    public List<User> getMyFridge() {
        System.out.println("getMyFridge");
        return userRepo.findAll();
    }
    //강의
    @GetMapping("/courses")
    public List<User> getCourses() {
        System.out.println("getCourses");
        return userRepo.findAll();
    }
    //질문
    @GetMapping("/faq")
    public List<User> getFaq() {
        System.out.println("getFaq");
        return userRepo.findAll();
    }


}



