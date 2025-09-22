package com.bproject.users.controller;

import com.bproject.users.entity.User;
import com.bproject.users.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userservice;

    public UserController(UserService userservice) {
        this.userservice = userservice;
    }

    /** 로그인 */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> req, HttpSession session) {
        String username = req.get("username");
        String password = req.get("password");

        User user = userservice.authenticate(username, password);

        Map<String, Object> res = new HashMap<>();
        if (user == null) {
            res.put("success", false);
            res.put("message", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return ResponseEntity.ok(res);
        }

        // 세션 저장 (SPRING_SESSION 안 씀: 기본 톰캣 세션)
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());

        res.put("success", true);
        res.put("message", "로그인 성공");
        return ResponseEntity.ok(res);
    }

    /** 로그아웃 (옵션) */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "로그아웃 성공");
        return ResponseEntity.ok(res);
    }

    /** 아이디(=username) 중복 확인 */
    @GetMapping("/check-nickname")
    public ResponseEntity<Map<String, Object>> checkNickname(@RequestParam("username") String username) {
        boolean available = userservice.isUsernameAvailable(username);
        Map<String, Object> res = new HashMap<>();
        if (available) {
            res.put("message", "사용 가능한 아이디입니다.");
            return ResponseEntity.ok(res);
        } else {
            res.put("message", "이미 사용 중인 아이디입니다.");
            return ResponseEntity.status(409).body(res);
        }
    }

    /** 회원가입 */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, Object> req) {
        Map<String, Object> res = new HashMap<>();
        try {
            long newId = userservice.register(req);
            res.put("success", true);
            res.put("userId", newId);
            res.put("message", "회원가입 성공!");
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            res.put("success", false);
            res.put("message", "입력 오류: " + e.getMessage());
            return ResponseEntity.badRequest().body(res);
        } catch (IllegalStateException e) {
            res.put("success", false);
            res.put("message", e.getMessage());
            return ResponseEntity.status(409).body(res);
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.internalServerError().body(res);
        }
    }
}
