package com.simple.vegan.controller;

import com.simple.vegan.entity.Role;
import com.simple.vegan.entity.User;
import com.simple.vegan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import javax.naming.CompoundName;
import javax.net.ssl.HandshakeCompletedEvent;
import java.time.LocalDate;
import java.util.Optional;


@RequiredArgsConstructor
@Controller
@RequestMapping("/user") // <- 클래스 레벨 매핑 추가
public class UserController {
    private final UserService userService;

    // 1. 사용자 목록
    @GetMapping("/")
    public String root() {
        return "redirect:/user/index";
    }

    @GetMapping("/index")
    public String userList(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        System.out.println("userindex");
        return "user/index";
    }


    // 2. 상세 페이지
    @GetMapping("/detail/{id}")
    public String userDetail(@PathVariable int id, Model model) {
        System.out.println("detail");
        User user = userService.getAllUsers()
                .stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);

        model.addAttribute("user", user);
        return "users/detail";
    }

    // 1. 회원가입 화면
    @GetMapping("/joinUser")
    public String showAddForm() {
        System.out.println("joinuser");
        return "user/joinUser";   // templates/user/joinUser.html
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
            Model model) {
        System.out.println("adduser");
        System.out.println("username:" + username);
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
        user.setRole(Role.USER);

        userService.addUser(user);
        model.addAttribute("successMessage", "회원가입이 완료되었습니다.");
        return "user/login";
    }


    //4. 로그인 화면
    @GetMapping("/login")
    public String showLoginForm() {
        System.out.println("showLoginForm");
        return "user/login"; // templates/users/login.html
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            Model model) {

        System.out.println("username: " + username);
        System.out.println("password: " + password);

        Optional<User> userOpt = userService.getUserByLoginId(username);

        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            HttpSession session = request.getSession(); //이 사용자는 누구인지  기억
            session.setAttribute("loginUser", userOpt.get()); //  Optional<User> userOpt 이값이 세션으로 들어감
            return "redirect:/user/index"; // 로그인 성공
        } else {
            model.addAttribute("error", "아이디 또는 비밀번호가 틀렸습니다.");
            return "user/login"; // 실패 → 다시 로그인 화면
        }
    }

    //로그아웃
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        System.out.println("logout");
        HttpSession session = request.getSession();
        if (session != null) {
            session.invalidate();
        }
        return "redirect:user/index";
    }


    // 5. 회원 탈퇴 화면 이동
    @GetMapping("/delete")
    public String showDeleteForm() {
        System.out.println("delete");
        return "user/delete";  // templates/user/delete.html
    }

    // 실제 탈퇴 처리
    @PostMapping("/delete")
    public String deleteUser(
            @RequestParam String username,
            @RequestParam String password,
            Model model) {
        System.out.println("delete");
        System.out.println("username: " + username);

        Optional<User> userOpt = userService.getUserByLoginId(username);

        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            userService.deleteById(userOpt.get().getId());
            return "redirect:/user/index";
        } else {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "user/delete";
        }
    }


    //회원 정보수정
    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable int id, Model model) {
        System.out.println("update-->");
        User user = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음: " + id));
        model.addAttribute("user", user);
        return "user/update";
    }

    // 회원 정보 수정 처리
    @PostMapping("/update")
    public String updateUser(
            @RequestParam int id,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone
            ) {

        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPhone(phone);

        userService.updateUser(user);

        return "redirect:/user/index";
    }

    @GetMapping("/mypage")
    public String showMypage(HttpSession session , Model model) {

        System.out.println("mypage");
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/user/login"; // 로그인 안되어 있으면 로그인 페이지로
        }
        model.addAttribute("user", loginUser);
        return "user/mypage"; // templates/user/mypage.html

    }
}
















