package com.vegan.controller;


import com.vegan.entity.User;
import com.vegan.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
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

    //로그인
    @GetMapping("/login")
    public String login() {
        System.out.println("login");
        return "/user/login";
    }

    //회원가입
    @GetMapping("/joinUser")
    public String joinUser(Model model) {
        System.out.println("joinUser");
        model.addAttribute("user", new User());
        return "/user/joinUser";
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
                return "redirect:/user/joinUser";
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
            return "redirect:/user/login";

        } catch (IllegalStateException e) {
            // 가입 실패 시 메시지 전달
            message.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user/joinUser";
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
            return "redirect:/user/login"; // 또는 그냥 return ""; 등
        }

        // 로그인 되어 있을 경우 삭제 로직 실행
        System.out.println("삭제 처리 - 로그인 사용자: " + loginUser.getUsername());

        // 삭제 후 리턴할 뷰
        return "/user/delete";

    }
    //내정보 보기
    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/user/login"; // 로그인 안 되어 있으면 리다이렉트
        }
        model.addAttribute("user", loginUser); // 뷰에서 ${user.username} 등 사용 가능
        return "user/mypage";
    }

    // 👉 내 정보 수정 보기
    @GetMapping("/myinfo")
    public String myinfo(HttpSession session, Model model) {
        System.out.println("myinfo" + session.getAttribute("loginUser"));
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/user/login"; // 로그인 안 되어 있으면 리다이렉트
        }
        model.addAttribute("user", loginUser); // 뷰에서 ${user.username} 등 사용 가능
        return "user/myinfo"; // 반드시 templates/user/myinfo.html 존재해야 함
    }


    // 👉 내 정보 수정 처리 (비밀번호 제외)
    @PostMapping("/myinfo")
    public String updateUser(@ModelAttribute User user, HttpSession session, RedirectAttributes redirectAttributes) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";

        // 1️⃣ DB에서 최신 사용자 정보 가져오기
        Optional<User> optionalUser = userService.findById(loginUser.getId());
        if (!optionalUser.isPresent()) return "redirect:/user/login";
        User dbUser = optionalUser.get();

        // 2️⃣ 폼 값이 비어있으면 DB 값을 사용 (즉, 수정하지 않은 필드는 그대로 유지)
        user.setUsername(isEmpty(user.getUsername()) ? dbUser.getUsername() : user.getUsername());
        user.setNickname(isEmpty(user.getNickname()) ? dbUser.getNickname() : user.getNickname());
        user.setPhone(isEmpty(user.getPhone()) ? dbUser.getPhone() : user.getPhone());
        user.setEmail(isEmpty(user.getEmail()) ? dbUser.getEmail() : user.getEmail());
        user.setBirthdate(user.getBirthdate() == null ? dbUser.getBirthdate() : user.getBirthdate());


        // 3️⃣ 비밀번호는 변경하지 않음
        user.setPassword(dbUser.getPassword());

        // 4️⃣ ID 설정
        user.setId(dbUser.getId());

        // 5️⃣ DB 업데이트
        User updatedUser = userService.update(user);

        // 6️⃣ 세션 갱신
        session.setAttribute("loginUser", updatedUser);

        // 7️⃣ 성공 메시지 전달
        redirectAttributes.addFlashAttribute("success", "정보가 수정되었습니다!");

        return "redirect:/user/myinfo";
    }

    // 🔹 유틸 메서드
    private boolean isEmpty(String str) {
        return str == null || str.isBlank();
    }

    // 🔹 비밀번호 변경창
    @GetMapping("/changePw")
    public String changePw(HttpSession session) {
        System.out.println("changePw");

        // 1. 로그인 여부 확인
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/user/login"; // 로그인 안 되어 있으면 로그인 페이지로 이동
        }

        // 2. 로그인 되어 있으면 changePw.html 뷰 반환
        return "user/changePw"; // templates/user/changePw.html
    }

    // GET 로그아웃 안내 화면
    @GetMapping("/logout")
    public String logoutScreen(@ModelAttribute("message") String message, Model model) {
        // FlashAttribute로 전달된 메시지를 그대로 화면에 보여줌
        model.addAttribute("message", message);
        return "/user/logout"; // logout.html
    }

    // POST 실제 로그아웃 처리
    @PostMapping("/logout")
    public String doLogout(HttpSession session, RedirectAttributes redirectAttributes) {
        // 1️⃣ 세션 무효화
        session.invalidate(); // 세션 초기화 → 더 이상 getAttribute 불가
        System.out.println("logout");
        // 2️⃣ 안내 메시지 전달 (FlashAttribute)
        redirectAttributes.addFlashAttribute("message", "성공적으로 로그아웃 되었습니다!");

        // 3️⃣ 안내 화면으로 리다이렉트
        return "redirect:/user/logout";
    }

    //강의
    @GetMapping("/courses")
    public String courses(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("user", loginUser);
        return "user/courses";
    }
    //게시판
    @GetMapping("/notice")
    public String notice(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("user", loginUser);
        return "user/notice";
    }
    //FAQ
    @GetMapping("/faq")
    public String faq(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("user", loginUser);
        return "user/faq";
    }
    //냉장고
    @GetMapping("/myFridge")
    public String myFridge(HttpSession session, Model model) {
        System.out.println("myFridge");
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("user", loginUser);
        return "user/myFridge";
    }
    //자유게시판
    @GetMapping("/freeBoard")
    public String freeBoard(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("user", loginUser);
        return "user/freeBoard";
    }


        }




