package com.vegan.controller;

import com.vegan.entity.Role;
import com.vegan.entity.User;
import com.vegan.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user") // <- 클래스 레벨 매핑 추가
public class UserController {
    private final UserService userService;

    @GetMapping("/")
    public String root() {
        System.out.println("user");
        return "redirect:/user/index";
    }

    //홈화면
    @GetMapping("/index")
    public String index() {
        System.out.println("index");
        return "/user/index";
    }

    //로그인 화면
    @GetMapping("/login")
    public String login() {
        System.out.println("login");
        return "user/login";
    }

    // HTTP POST 방식으로 /login 요청을 처리하는 메서드(로그인)
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        // UserService를 통해 username으로 사용자 조회
        Optional<User> userOpt = userService.getUserByLoginId(username);

        // 사용자가 존재하고 비밀번호가 일치하는지 확인
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            // 로그인 성공 → 세션에 사용자 정보 저장
            session.setAttribute("loginUser", userOpt.get());

            // 서버에서 리다이렉트 → /user/index 페이지로 이동
            return "redirect:/user/index";
        } else {
            // 로그인 실패 → 에러 메시지를 뷰에 전달
            model.addAttribute("error", "Invalid username or password");

            // 다시 로그인 화면(user/login.html)으로 이동
            return "user/login";
        }
    }

    // 내정보 (마이페이지)
    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model) {
        // 세션에서 로그인한 사용자 정보 가져오기
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            // 로그인하지 않은 상태 → 로그인 페이지로 리다이렉트
            return "redirect:/user/login";
        }

        // 로그인 상태 → 사용자 정보를 뷰에 전달
        model.addAttribute("user", loginUser);

        System.out.println("mypage: " + loginUser.getUsername());
        return "user/mypage";  // templates/user/mypage.html
    }

    //회원탈퇴
    @GetMapping("/delete")
    public String delete() {
        System.out.println("delete");
        return "user/delete";
    }

    //내정보 수정
    // 내 정보 수정 화면
    @GetMapping("/myinfo")
    public String myinfo(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/user/login"; // 로그인 안 했으면 로그인 페이지로
        }
        model.addAttribute("user", loginUser); // 현재 정보 폼에 뿌려줌
        return "user/myinfo";
    }

    // 회원 정보 수정 처리
    @PostMapping("/myinfo")
    public String updateUser(User user, HttpSession session, Model model) {
        // 세션에서 로그인한 사용자 확인
        System.out.println("비밀번호 변경");
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/user/login"; // 로그인 안 되어 있으면 로그인 페이지로
        }

        // id는 세션에서 가져옴 (폼에 노출 안 됨)
        user.setId(loginUser.getId());

        try {
            userService.updateUser(user); // 서비스 호출 (중복체크 포함 가능)
            session.setAttribute("loginUser", user); // 세션 갱신
            model.addAttribute("message", "정보가 수정되었습니다.");
            return "redirect:/user/myinfo"; // 성공 시 내 정보 페이지로 리다이렉트
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage()); // 에러 메시지 뷰로 전달
            model.addAttribute("user", loginUser); // 기존 정보 유지
            return "user/myinfo"; // 같은 페이지에서 에러 출력
        }
    }
    // 비밀번호 변경 화면(GET)
    @GetMapping("/changePw")
    public String changePwPage() {
        System.out.println("changePw");
        return "user/changePw";
    }
    // 비밀번호 변경 처리(POST)
    @PostMapping("/changePw")
    public String changePassword(
            @RequestParam String nowPw,
            @RequestParam String password,
            @RequestParam String confirmPw,
            HttpSession session,
            Model model) {
        System.out.println("새 비밀번호"+confirmPw);
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/user/login";
        }

        if (!password.equals(confirmPw)) {
            model.addAttribute("error", "새 비밀번호와 확인이 일치하지 않습니다.");
            return "user/changePw";
        }

        if (!loginUser.getPassword().equals(nowPw)) {
            model.addAttribute("error", "현재 비밀번호가 일치하지 않습니다.");
            return "user/changePw";
        }

        try {
            userService.updatePassword(loginUser.getId(), password);
            loginUser.setPassword(password); // 세션 갱신
            session.setAttribute("loginUser", loginUser);
            return "redirect:/user/myinfo"; // 성공 시 myinfo로 이동
        } catch (Exception e) {
            model.addAttribute("error", "비밀번호 변경 실패: " + e.getMessage());
            return "user/changePw";
        }
    }
    //회원가입
    @GetMapping("/joinUser")
    public String joinUser() {
        System.out.println("joinUser");
        return "user/joinUser";
    }


    //  회원가입 처리
    @PostMapping("/joinUser")
    public String addUser(
            @RequestParam String username, // 아이디
            @RequestParam String nickname,
            @RequestParam String password,
            @RequestParam(required = false) String birthdate,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role, // 수정
            Model model) {

        System.out.println("addUser");
        System.out.println("username: " + username);

        // 중복 체크
        if (userService.getUserByLoginId(username).isPresent()) {
            model.addAttribute("errorMessage", "이미 존재하는 아이디입니다.");
            return "user/joinUser";
        }

        User user = new User();
        user.setUsername(username);
        user.setNickname(nickname);
        user.setPassword(password);

        if (birthdate != null && !birthdate.isBlank()) {
            user.setBirthdate(LocalDate.parse(birthdate));
        }

        user.setPhone(phone);
        user.setEmail(email);

        userService.addUser(user);
        model.addAttribute("successMessage", "회원가입이 완료되었습니다.");
        return "user/login";
    }
}










