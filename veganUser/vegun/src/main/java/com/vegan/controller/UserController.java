package com.vegan.controller;

import com.vegan.entity.Role;
import com.vegan.entity.User;
import com.vegan.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user") // <- 클래스 레벨 매핑 추가
public class UserController {
    private final UserService userService;

    // 👉 기본 URL(/user/) 접근 시 /user/index 로 리다이렉트
    public String root(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/user/login"; // 로그인 안 한 경우
        }

        if (loginUser.getRole() == Role.ADMIN) {
            return "redirect:/admin/indexmg";
        } else if (loginUser.getRole() == Role.USER) {
            return "redirect:/user/index";
        } else if (loginUser.getRole() == Role.INSTRUCTOR) {
            return "redirect:/instructor/index";
        } else {
            return "redirect:/error"; // 알 수 없는 권한
        }
    }

    // 👉 사용자 메인 페이지(index.html) 보여주기
    @GetMapping("/index")
    public String index() {
        return "/user/index";
    }

    // 👉 로그인 화면 열기
    @GetMapping("/login")
    public String login() {
        System.out.println("login");
        return "user/login";
    }
    // 👉 로그아웃 처리
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        System.out.println("logout");
        session.invalidate(); // 현재 세션 무효화 (로그인 정보 삭제)
        return "redirect:/user/login"; // 로그아웃 후 로그인 페이지로 이동
    }




    // 👉 마이페이지 보기 (로그인 상태에서만 접근 가능)
    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login"; // 로그인 안 했으면 로그인 페이지로
        model.addAttribute("user", loginUser);
        return "user/mypage";
    }

    // 👉 내 정보 보기
    @GetMapping("/myinfo")
    public String myinfo(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";
        model.addAttribute("user", loginUser);
        return "user/myinfo";
    }

    // 👉 내 정보 수정하기
    @PostMapping("/myinfo")
    public String updateUser(User user, HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";

        user.setId(loginUser.getId()); // 내 정보 수정 시 id 유지

        try {
            userService.updateUser(user); // DB 업데이트
            session.setAttribute("loginUser", user); // 세션도 업데이트
            return "redirect:/user/myinfo";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", loginUser);
            return "user/myinfo";
        }
    }

    // 👉 비밀번호 변경 페이지 열기
    @GetMapping("/changePw")
    public String changePwPage() {
        return "user/changePw";
    }

    // 👉 비밀번호 변경 처리
    @PostMapping("/changePw")
    public String changePassword(@RequestParam String nowPw,
                                 @RequestParam String password,
                                 @RequestParam String confirmPw,
                                 HttpSession session,
                                 Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";

        // 새 비밀번호 확인
        if (!password.equals(confirmPw)) {
            model.addAttribute("error", "새 비밀번호와 확인이 일치하지 않습니다.");
            return "user/changePw";
        }

        // 현재 비밀번호 확인
        if (!loginUser.getPassword().equals(nowPw)) {
            model.addAttribute("error", "현재 비밀번호가 일치하지 않습니다.");
            return "user/changePw";
        }

        try {
            userService.updatePassword(loginUser.getId(), password); // DB 업데이트
            loginUser.setPassword(password); // 세션도 변경
            session.setAttribute("loginUser", loginUser);
            return "redirect:/user/myinfo";
        } catch (Exception e) {
            model.addAttribute("error", "비밀번호 변경 실패: " + e.getMessage());
            return "user/changePw";
        }
    }

    // 👉 회원가입 페이지 열기
    @GetMapping("/joinUser")
    public String joinUser() {
        return "user/joinUser";
    }

    // 👉 회원가입 요청 처리
    @PostMapping("/joinUser")
    public String addUser(@RequestParam String username,
                          @RequestParam String name, // name 추가
                          @RequestParam String nickname,
                          @RequestParam String password,
                          @RequestParam(required = false) String birthdate,
                          @RequestParam(required = false) String phone,
                          @RequestParam(required = false) String email,
                          @RequestParam(required = false) String role,
                          Model model) {

        // 아이디 중복 체크
        if (userService.getUserByLoginId(username).isPresent()) {
            model.addAttribute("errorMessage", "이미 존재하는 아이디입니다.");
            return "user/joinUser";
        }

        // User 객체 생성 후 값 저장
        User user = new User();
        user.setUsername(username);
        user.setName(name); // 여기서 name 설정
        user.setNickname(nickname);
        user.setPassword(password);
        if (birthdate != null && !birthdate.isBlank())
            user.setBirthdate(LocalDate.parse(birthdate));
        user.setPhone(phone);
        user.setEmail(email);
        user.setRole(role != null ? Role.valueOf(role.toUpperCase()) : Role.USER);

        // DB 저장
        userService.addUser(user);
        model.addAttribute("successMessage", "회원가입이 완료되었습니다.");
        return "user/login";
    }
    @GetMapping("/delete")
    public String deleteUser() {
        System.out.println("deleteUser");
        return "/user/index";
    }
}










