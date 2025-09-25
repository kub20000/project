package com.vegan.service;

import com.vegan.entity.Post;
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


    // 👉 아이디로 사용자 조회
    public Optional<User> getUserByLoginId(String username) {
        return userRepo.findByUsername(username);
    }

    // 👉 username으로 사용자 조회
    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    // 👉 ID로 사용자 조회
    public Optional<User> getUserById(int id) {
        return userRepo.findById(id);
    }

    // 👉 전체 사용자 목록 조회
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    // 👉 회원가입 (중복체크 후 저장)
    public void addUser(User user) {
        if (userRepo.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        userRepo.save(user);
    }

    // 👉 내 정보 수정
    public void updateUser(User user) {
        Optional<User> existing = userRepo.findByUsername(user.getUsername());
        if (existing.isPresent() && existing.get().getId() != user.getId()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        userRepo.updateUser(user);
    }

    // 👉 비밀번호 변경
    public void updatePassword(int id, String newPassword) {
        int result = userRepo.updatePassword(id, newPassword);
        if (result == 0) throw new IllegalArgumentException("비밀번호 변경 실패: 사용자 없음");
    }

    // 👉 회원탈퇴
    public void deleteUser(String username) {
        userRepo.deleteByUsername(username);
    }

    public boolean deleteUserById(int id) {
        int result = userRepo.deleteById(id); // Repository 호출
        return result > 0; // 1 이상이면 삭제 성공

    }
}






