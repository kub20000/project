package com.bproject.user.controller;

import com.bproject.user.entity.Post;
import com.bproject.user.entity.User;
import com.bproject.user.service.AdminPostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return "admin/postM"; // templates/admin/post.html 렌더링
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
        return "redirect:/admin/postM";   // 등록 후 공지사항 목록 페이지로 이동
    }

    // 1️⃣ 공지사항 수정 화면 (GET)
    @GetMapping("/post-edit")
    public String editPostForm(@RequestParam Long id, Model model) {
        System.out.println("post-edit " + id);

        Post post = postService.findById(id);
        if (post == null) {
            System.out.println("해당 ID의 공지사항이 존재하지 않습니다: " + id);
            return "redirect:/admin/postM"; // 목록 페이지로 이동
        }

        model.addAttribute("post", post); // post-edit.html에서 th:object="${post}"로 접근
        return "admin/post-edit"; // 수정 화면 반환
    }

    // 2️⃣ 공지사항 수정 처리 (POST)
    @PostMapping("/post-edit")
    public String updatePost(@ModelAttribute Post post) {
        System.out.println("post-edit POST 호출");
        postService.update(post); // DB 업데이트
        return "redirect:/admin/postM"; // 수정 후 목록 페이지로 이동
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



    // 자유게시판 전체 조회 (검색 + 페이징)
    @GetMapping("/posts")
    public ResponseEntity<Map<String, Object>> getPosts(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int limit) {

        List<Post> allPosts = postService.getAllPosts();

        // 검색 필터링
        List<Post> filtered = allPosts.stream()
                .filter(p -> p.getTitle().toLowerCase().contains(search.toLowerCase()))
                .toList();

        // 페이징 처리
        int total = filtered.size();
        int totalPages = (int) Math.ceil((double) total / limit);
        int start = (page - 1) * limit;
        int end = Math.min(start + limit, total);

        List<Post> pagePosts = filtered.subList(start, end);

        Map<String, Object> response = new HashMap<>();
        response.put("posts", pagePosts);
        response.put("totalPages", totalPages);

        return ResponseEntity.ok(response);
    }


    //자유게시판
    @GetMapping("/freepost-delete")
    public String freepost(@RequestParam(required = false) Long id, Model model) {
        System.out.println("freepost-delete");

        if (id != null) {
            Post post = postService.findById(id);
            if (post == null) throw new RuntimeException("게시글 불러오기 실패");
            model.addAttribute("post", post);
        }

        List<Post> posts = postService.getAllPosts(); // 자유게시판 전체 목록
        model.addAttribute("posts", posts);

        return "admin/freepost-delete";
    }





}

