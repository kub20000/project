package com.vegan.controller;


import com.vegan.entity.Post;
import com.vegan.entity.User;
import com.vegan.service.AdminPostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminPostController {
    private final AdminPostService postService;


    // 1️⃣ 글 작성/수정 화면 열기
    @GetMapping("/post")
    public String showPostForm(@RequestParam(required = false) Long id, Model model) {
        // id가 존재하면 기존 게시글을 불러와 수정 화면에 표시
        if (id != null) {
            Post post = postService.findById(id); // DB에서 게시글 조회
            model.addAttribute("post", post);     // 모델에 담아 뷰에 전달
        } else {
            model.addAttribute("post", new Post()); // id 없으면 신규 글 작성용 객체
        }
        return "admin/post"; // templates/admin/post.html 렌더링
    }
    // 2️⃣ 공지사항 추가 화면
    @GetMapping("/addNotice")
    public String addNoticeForm(Model model) {
        model.addAttribute("post", new Post()); // 빈 Post 객체 전달
        return "admin/addNotice";               // templates/admin/addNotice.html 렌더링
    }

    // 3️⃣ 공지사항 등록 처리
    @PostMapping("/addNotice")
    public String addNoticeSubmit(@ModelAttribute Post post, HttpServletRequest request) { //세션에 저장된 로그인 가져오기
        HttpSession session = request.getSession();//가져온거 받기
        User loginUser = (User) session.getAttribute("loginUser"); //객체 만들기
        postService.add(post, loginUser);  //서비스에 포스트,유저 보내기
        return "redirect:/admin/post";   // 등록 후 공지사항 목록 페이지로 이동
    }
    //공지 사항 추가
    @GetMapping("/post-edit")
    public String editPostForm(@RequestParam Long id, Model model) {
        System.out.println("post-edit"+id);
        Post post = postService.findById(id);
        model.addAttribute("post", post);
        return "admin/post-edit";
    }

    // 6️⃣ 게시글 화면 열기
    @GetMapping("/post-view")
    public String viewPost(@RequestParam Long id, Model model) {
        System.out.println("post id " + id);
        Post post = postService.findById(id);      // DB에서 게시글 조회
        if (post == null) throw new RuntimeException("게시글이 존재하지 않습니다."); // 없으면 예외
        model.addAttribute("post", post);          // 모델에 담아 뷰로 전달
        return "admin/post-view";                  // templates/admin/post-view.html 렌더링
    }
    @GetMapping("/uploadCourse")
    public String uploadCourseForm() {
        System.out.println("uploadCourseForm");
        return "admin/uploadCourse";

    }
    @GetMapping("/freepost-delete")
    public String freepostDeleteForm() {
        System.out.println("freepostDeleteForm");
        return "admin/freepost-delete";
    }









}
