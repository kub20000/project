package com.vegan;

import com.vegan.entity.Role;
import com.vegan.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String root(HttpSession session) {
        // 세션에서 로그인한 사용자 정보 가져오기
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            // 로그인 안 되어 있으면 로그인 페이지로
            return "redirect:/user/login";
        }

        // Role에 따라 리다이렉트
        switch (loginUser.getRole()) {
            case ADMIN:
                return "redirect:/admin/indexmg";       // 관리자 홈
            case USER:
                return "redirect:/user/index";          // 일반 사용자 홈
            case INSTRUCTOR:
                return "redirect:/instructor/homeT";   // 강사 홈
            default:
                return "redirect:/error";               // 알 수 없는 권한
        }
    }
}
