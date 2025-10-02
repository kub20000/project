package com.vegan.controller;

import com.vegan.entity.Role;
import com.vegan.entity.User;
import com.vegan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin") // <- 클래스 레벨 매핑 추가
public class AdminController {
    private final UserService userService;

    //홈화면
    @GetMapping("/indexmg")
    public String index() {
        System.out.println("admin indexmg");
        return "/admin/indexmg";
    }

    //회원관리
    // GET 회원관리 페이지
    @GetMapping("/usermng")
    public String usermngPage() {
        System.out.println("회원관리 페이지 이동");
        return "/admin/usermng"; // Thymeleaf에서 tbody는 JS로 채움
    }


    // 1️⃣ 회원 수정 폼(GET)
    // /admin/edit/{id} 로 접근하면 해당 회원 정보를 불러와 수정 폼에 전달
    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable int id, Model model) {
        System.out.println("admin edit " + id);

        Optional<User> optionalUser = userService.getUserById(id);

        // 해당 회원이 없으면 회원관리 페이지로 리다이렉트
        if (optionalUser.isEmpty()) return "redirect:/admin/usermng";

        // User 객체를 Thymeleaf에 전달 (폼에 기존 값 표시)
        model.addAttribute("user", optionalUser.get());
        return "admin/edit"; // templates/admin/edit.html
    }

    // 2️⃣ 회원 정보 수정 처리(POST)
    @PostMapping("/edit")
    public String updateUser(@ModelAttribute User user) {
        System.out.println("id");
        Optional<User> existingUserOpt = userService.getUserById(user.getId());
        if (existingUserOpt.isPresent()) {
            User dbUser = existingUserOpt.get();

            // 1️⃣ username 유지
            if (user.getUsername() == null || user.getUsername().isEmpty()) {
                user.setUsername(dbUser.getUsername());
            }

            // 2️⃣ password 유지: 입력값이 없으면 기존 DB 값 사용
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword(dbUser.getPassword());
            }
            if(user.getName() == null || user.getName().isEmpty()) {
                user.setName(dbUser.getName());
            }

            // 3️⃣ nickname 유지 (외래 키 참조 등으로 수정 금지)
            user.setNickname(dbUser.getNickname());

            // 4️⃣ role 유지
            if (user.getRole() == null) {
                user.setRole(dbUser.getRole());
            }

            // 5️⃣ 나머지 필드 업데이트
            userService.update(user);
        }
        return "redirect:/admin/usermng";
    }

}


















