package com.simple.vegan.service;

import com.simple.vegan.entity.Role;
import com.simple.vegan.entity.User;
import com.simple.vegan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;

    public UserRepository getUserRepo() {
        return userRepo;
    }

    // 1. 전체 사용자 조회
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    // username으로 조회
    public Optional<User> getUserByLoginId(String username) {
        return userRepo.findByUsername(username);
    }

    // 회원가입
    public void addUser(User user) {
        userRepo.save(user); // Repository에 User 객체 그대로 전달
    }

    //회원 탈퇴
    public void deleteById(int id) {
        userRepo.deleteById(id);
    }

    //회원 수정 (wjsekf)
    public void updateUser(User user) {
        userRepo.update(user); //userRepo.update(user)를 호출 → Repository로 전달
    }

    //DB에서 해당 id의 사용자가 존재하면 User 객체를 담고,
    //존재하지 않으면 빈 Optional을 반환
    public Optional<User> getUserById(int id) {
        return userRepo.findById(id);  // Repository에서 Optional<User> 반환
    }
}
