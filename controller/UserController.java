package com.bproject.user.controller;

import com.bproject.user.entity.User;
import com.bproject.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Controller
 //@RequestMapping("/user") // <- 클래스 레벨 매핑 추가
public class UserController {
    private final UserService userService;

   @GetMapping("/mainhome.")
   public String index() {
       System.out.println("index");
       return "/mainhome";
   }

    //로그인
    @GetMapping("/login")
    public String login() {
        System.out.println("login");
        return "mypage/login";
    }

    //회원가입
    @GetMapping("/joinUser")
    public String joinUser(Model model) {
        System.out.println("joinUser");
        model.addAttribute("user", new User());
        return "mypage/joinUser";
    }

    // 회원가입 처리
    @PostMapping("/joinUser")
    public String joinUser(@ModelAttribute User user, RedirectAttributes message) {
        System.out.println("joinUser = " + user);

        try {
            // 1️⃣ 생년월일 처리: form에서 넘어오는 yyyy-MM-dd 문자열을 LocalDate로 변환
            // 이미 User.birthdate가 LocalDate라면 Spring이 자동 매핑 가능하지만, null 체크 필수
            if (user.getBirthdate() == null) {
                message.addFlashAttribute("errorMessage", "생년월일을 입력해주세요.");
                return "redirect:/joinUser";
            }

            // 2️⃣ 전화번호 처리: 하이픈 제거
            if (user.getPhone() != null) {
                user.setPhone(user.getPhone().replaceAll("-", "").trim());
            }

            // 3️⃣ 생성일 세팅 (JPA 아님)
            user.setCreatedAt(LocalDateTime.now());

            // 4️⃣ 회원가입 서비스 호출
            userService.join(user);

            // 5️⃣ 성공 시 로그인 페이지로 이동
            return "redirect:/login";

        } catch (IllegalStateException e) {
            // 가입 실패 시 메시지 전달
            message.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/joinUser";
        }
    }


    //회원탈퇴
    @GetMapping("/delete")
    public String delete(HttpSession session) {
        // 세션에서 로그인 정보 가져오기
        System.out.println("delete" + session.getAttribute("loginUser"));
        User loginUser = (User) session.getAttribute("loginUser");

        // 로그인 안 되어 있으면 아무 동작 없이 메서드 종료
        if (loginUser == null) {
            return "redirect:/login"; // 또는 그냥 return ""; 등
        }

        // 로그인 되어 있을 경우 삭제 로직 실행
        System.out.println("삭제 처리 - 로그인 사용자: " + loginUser.getUsername());

        // 삭제 후 리턴할 뷰
        return "mypage/delete";

    }
    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login"; // 로그인 안 되어 있으면 리다이렉트
        }
        model.addAttribute("user", loginUser); // 뷰에서 ${user.username} 등 사용 가능
        return "mypage/dashBoard";
    }

    // 👉 내 정보 수정 보기
    @GetMapping("/myinfo")
    public String myinfo(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login"; // 로그인 안 되어 있으면 리다이렉트
        }
        model.addAttribute("user", loginUser); // 뷰에서 ${user.username} 등 사용 가능
        return "mypage/myinfo"; // 반드시 templates/user/myinfo.html 존재해야 함
    }


    // 👉 내 정보 수정하기
    @PostMapping("/myinfo")
    public String updateUser(@ModelAttribute User user, HttpSession session, RedirectAttributes redirectAttributes) {

        // 1️⃣ 로그인 사용자 확인
        // 세션에서 로그인한 User 객체(loginUser)를 가져옵니다.
        // 로그인 상태가 아니면 로그인 페이지로 리다이렉트
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        // 2️⃣ 빈 값 처리
        // 폼에서 전달된 값이 null 또는 빈 문자열이면 기존 세션(loginUser)의 값을 사용
        // 즉, 사용자가 입력하지 않은 필드는 기존 값 유지
        user.setUsername(isEmpty(user.getUsername()) ? loginUser.getUsername() : user.getUsername());
        user.setNickname(isEmpty(user.getNickname()) ? loginUser.getNickname() : user.getNickname());
        user.setPassword(isEmpty(user.getPassword()) ? loginUser.getPassword() : user.getPassword());
        user.setPhone(isEmpty(user.getPhone()) ? loginUser.getPhone() : user.getPhone());
        user.setEmail(isEmpty(user.getEmail()) ? loginUser.getEmail() : user.getEmail());
        user.setBirthdate(user.getBirthdate() == null ? loginUser.getBirthdate() : user.getBirthdate());

        // 3️⃣ ID 설정
        // DB 업데이트 시 로그인한 사용자의 ID를 기준으로 수정
        user.setId(loginUser.getId());

        // 4️⃣ DB 업데이트
        // UserService를 통해 DB에 회원 정보를 수정하고, 수정된 User 객체를 반환받음
        User updatedUser = userService.update(user);

        // 5️⃣ 세션 갱신
        // 수정된 정보를 세션에 다시 저장 → 이후 페이지에서 최신 정보 표시 가능
        session.setAttribute("loginUser", updatedUser);

        // 6️⃣ 성공 메시지 전달
        // RedirectAttributes를 사용해 일회성 메시지를 전달
        redirectAttributes.addFlashAttribute("success", "정보가 수정되었습니다!");

        // 7️⃣ 마이인포 페이지로 리다이렉트
        return "redirect:/myinfo";
    }

    // 8️⃣ 유틸 메서드
// 문자열이 null이거나 공백일 경우 true 반환
    private boolean isEmpty(String str) {
        return str == null || str.isBlank();
    }
    //비밀번호 변경창 열기
    @GetMapping("/changePw")
    public String changePw() {
        System.out.println("changePw");
        return "mypage/changePw";
    }



    // 🔹 비밀번호 변경창
    @PostMapping("/changePw")
    public String changePassword(@RequestParam String newPassword,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        // 암호화 없이 그대로 저장
        loginUser.setPassword(newPassword);

        userService.update(loginUser);
        session.setAttribute("loginUser", loginUser);

        redirectAttributes.addFlashAttribute("success", "비밀번호가 변경되었습니다!");
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(){
        return "redirect:home";
    }



}


