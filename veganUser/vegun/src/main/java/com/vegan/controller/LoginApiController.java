package com.vegan.controller;

import com.vegan.entity.User;
import com.vegan.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api") // API 전용 경로
public class LoginApiController {

    private final UserService userService;

    public LoginApiController(UserService userService) {
        this.userService = userService;
    }


    // 로그인 요청 처리
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request, HttpSession session) {
        System.out.println("username : " + request.getUsername());
        System.out.println("password : " + request.getPassword());
        Optional<User> userOpt = userService.getUserByLoginId(request.getUsername());

        if (userOpt.isPresent() && userOpt.get().getPassword().equals(request.getPassword())) {
            // 로그인 성공 → 세션에 사용자 정보 저장
            session.setAttribute("loginUser", userOpt.get());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Login successful"
            ));
        } else {
            // 로그인 실패
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Invalid username or password"
            ));
        }
    }

    // 로그인 요청 DTO
    public static class LoginRequest {
        private String username;
        private String password;

        // getter & setter
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

    }

    //회원 삭제
    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteUser(@RequestBody DeleteRequest request) {
        System.out.println("username : " + request.getUsername());
        System.out.println("password : " + request.getPassword());
        Optional<User> userOpt = userService.findByUsername(request.getUsername());

        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "탈퇴 실패"));
        }

        try {
            userService.deleteUser(userOpt.get().getUsername()); // username 전달
            return ResponseEntity.ok(Map.of("message", "탈퇴 완료"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "서버 오류"));
        }
    }

    //회원 삭제 처리
    public static class DeleteRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
    }

}

