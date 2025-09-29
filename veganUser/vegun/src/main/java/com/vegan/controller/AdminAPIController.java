package com.vegan.controller;


import com.vegan.entity.User;
import com.vegan.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")// API 전용 경로
public class AdminAPIController {
    private final UserService userService;

    public AdminAPIController(UserService userService) {
        this.userService = userService;
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> data,
            HttpSession session) {

        String username = data.get("username");
        String password = data.get("password");

        Optional<User> loginResult = userService.login(username, password);

        if (loginResult.isPresent()) {
            User loginUser = loginResult.get();

            // 세션에 로그인 사용자 정보 저장
            session.setAttribute("loginUser", loginUser);
            session.setAttribute("role", loginUser.getRole());

            // 프론트 기준 JSON: success, role
            Map<String, Object> response = Map.of(
                    "success", true,
                    "role", loginUser.getRole()
            );

            return ResponseEntity.ok(response);

        } else {
            // 로그인 실패 시 JSON: success=false, message
            Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "아이디 또는 비밀번호가 올바르지 않습니다."
            );

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    // 회원 탈퇴 처리(로그인 상태일 때만 가능)
    @PostMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestBody Map<String, Object> data, HttpSession session) {
        // 1. 로그인 사용자 확인
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            // 로그인 안되어 있으면 401 Unauthorized 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        try {
            // 2. 서비스 호출 -> 회원 탈퇴 처리
            userService.deleteUser(loginUser.getId());

            // 3. 세션에서 로그인 정보 제거
            session.invalidate();

            // 4. 성공 메시지 반환
            return ResponseEntity.ok(Map.of("message", "회원 탈퇴 완료"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "회원 탈퇴 중 오류가 발생했습니다."));
        }
    }
    //새비밀번호  처리
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> data, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser"); // 로그인 세션 확인
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        String nowPw = data.get("nowPw");
        String newPw = data.get("newPw");

        try {
            // 현재 비밀번호 검증 및 변경
            userService.changePassword(loginUser.getId(), nowPw, newPw);

            // 세션에 있는 비밀번호도 갱신
            loginUser.setPassword(newPw);
            session.setAttribute("loginUser", loginUser);

            return ResponseEntity.ok(Map.of("message", "비밀번호 변경 성공"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "비밀번호 변경 중 오류 발생"));
        }
    }
    // 회원관리 처리 (API)
    @GetMapping("/usermng")
    public ResponseEntity<?> getUsers(@RequestParam(required = false) Integer id) {
        // 1️⃣ 전체 회원 목록 조회
        List<User> users = userService.getAllUsers();

        // 2️⃣ 특정 id가 전달되면 해당 회원만 반환
        if (id != null) {
            User user = users.stream()
                    .filter(u -> u.getId() == id)
                    .findFirst()
                    .orElse(null);

            if (user != null) {
                // 2-1️⃣ 단일 회원 반환
                return ResponseEntity.ok(user);
            } else {
                // 2-2️⃣ 회원이 존재하지 않을 경우 404 반환
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "회원이 존재하지 않습니다."));
            }
        }

        // 3️⃣ id가 없으면 전체 회원 목록 반환
        return ResponseEntity.ok(users);
    }

    //회원삭제
    @DeleteMapping("/usermng/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        Optional<User> optionalUser = userService.getUserById(id);

        if (optionalUser.isEmpty()) {
            // 1️⃣ 회원이 존재하지 않으면 404 반환
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "회원이 존재하지 않습니다."));
        }

        // 2️⃣ 회원 삭제
        userService.deleteUser(id);

        // 3️⃣ 삭제 성공 메시지 반환
        return ResponseEntity.ok(Map.of("message", "삭제 완료"));
    }





}
