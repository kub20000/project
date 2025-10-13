package com.bproject.user.controller;

import com.bproject.user.entity.User;
import com.bproject.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api") // API 전용 경로
public class LoginApiController {
    private final UserService userService;

    public LoginApiController(UserService userService) {

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
            session.setAttribute("userId", loginUser.getId());
            session.setAttribute("role", loginUser.getRole());

            // 성공 응답 JSON
            Map<String, Object> response = Map.of(
                    "success", true,
                    "role", loginUser.getRole()
            );

            return ResponseEntity.ok(response);
        } else {
            // 실패 응답 JSON
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
    // 회원가입 처리
    // -----------------------
    @PostMapping("/joinUser")
    public String joinUser(@ModelAttribute User user,
                           RedirectAttributes redirectAttributes) {
        try {
            // 1️⃣ 회원가입 서비스 호출
            userService.join(user);
            // 2️⃣ 성공 시 로그인 페이지로 이동
            redirectAttributes.addFlashAttribute("success", "회원가입이 완료되었습니다!");
            return "redirect:/login/login";
        } catch (IllegalStateException e) {
            // 3️⃣ 중복 아이디/닉네임 발생 시
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/login/joinUser";
        }
    }


    // 아이디 중복 체크 (AJAX)
    @ResponseBody
    @GetMapping("/check-username")
    public Map<String, Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return Map.of("exists", exists);
    }

    // 닉네임 중복 체크 (AJAX)
    @ResponseBody
    @GetMapping("/check-nickname")
    public Map<String, Boolean> checkNickname(@RequestParam String nickname) {
        boolean exists = userService.existsByNickname(nickname);
        return Map.of("exists", exists);
    }


}