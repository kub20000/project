package com.vegan.controller;

import com.vegan.entity.Notice;
import com.vegan.entity.Post;
import com.vegan.entity.User;
import com.vegan.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

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
    @GetMapping("/usermng")
    public String usermng() {
        System.out.println("회원관리");
        return "/admin/usermng";
    }

    //회원관리 처리
    @PostMapping("/usermng")
    public String usermng(@RequestParam(required = false) Integer id, Model model) {
        System.out.println("usermng" + id);
        if (id != null) {
            User user = userService.getAllUsers()
                    .stream()
                    .filter(u -> u.getId() == id)
                    .findFirst()
                    .orElse(null);

            model.addAttribute("user", user);
        }
        return "admin/usermng";
    }

    // 회원 수정 폼(GET)
    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable int id, Model model) {
        System.out.println("admin edit " + id);

        Optional<User> optionalUser = userService.getUserById(id);
        if (optionalUser.isEmpty()) return "redirect:/admin/usermng";

        model.addAttribute("user", optionalUser.get()); // User 객체 전달
        return "admin/edit"; // templates/admin/edit.html
    }

    // 회원 정보 수정 처리(POST)
    @PostMapping("/edit")
    public String updateUser(@ModelAttribute User user) {
        Optional<User> existingUserOpt = userService.getUserById(user.getId());
        if (existingUserOpt.isPresent()) {
            User dbUser = existingUserOpt.get();

            // 1. 비밀번호 유지
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword(dbUser.getPassword());
            }

            // 2. nickname 유지 (외래 키 참조 때문에 수정 금지)
            user.setNickname(dbUser.getNickname());

            // 3. 다른 필드 업데이트
            userService.updateUser(user);
        }
        return "redirect:/admin/usermng";
    }


}












