package com.vegan.controller;


import com.vegan.entity.Role;
import com.vegan.entity.User;
import com.vegan.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/api") // API 전용 경로
public class LoginApiController {

    private final UserService userService;

    public LoginApiController(UserService userService) {
        this.userService = userService;
    }

    // 로그인 요청 처리
    // 👉 로그인 (REST API 버전, JSON 응답)
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request, HttpSession session) {
        Optional<User> userOpt = userService.getUserByLoginId(request.getUsername());
        String pwd = userOpt.get().getPassword();

        if (pwd.equals(request.getPassword())) {
            User user = userOpt.get();
            session.setAttribute("loginUser", user);
            System.out.println("id"+user.getId());
            System.out.println("username"+user.getUsername());
            // 로그인 성공 후 이동할 URL 지정
            String redirectUrl = user.getRole() == Role.ADMIN ? "/admin/indexmg" : "/user/index";

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "로그인 성공",
                    "redirect", redirectUrl
            ));
        } else {
            System.out.println("username**"+request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "아이디 또는 비밀번호가 올바르지 않습니다."
            ));
        }
    }

    // 👉 회원탈퇴 API
    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteUser(@RequestBody DeleteRequest request) {
        System.out.println("delete"+id);
        Optional<User> userOpt = userService.findByUsername(request.getUsername());

        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "탈퇴 실패"));
        }

        try {
            userService.deleteUser(request.getUsername());
            return ResponseEntity.ok(Map.of("message", "탈퇴 완료"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 오류"));
        }
    }

    // ✅ DTO (요청 받을 때 사용하는 객체)
    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    public static class DeleteRequest {
        private String username;
        private String password;
    }


}
