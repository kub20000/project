package com.vegan.controller;

import com.vegan.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/instructor")
public class InstructorController {
    // 홈 화면
    @GetMapping("/homeT")
    public String index(HttpSession session, Model model) {
        System.out.println("homeT");
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser != null) {
            model.addAttribute("user", loginUser);
        }
        return "instructor/homeT"; // 루트 슬래시 제거
    }
}
