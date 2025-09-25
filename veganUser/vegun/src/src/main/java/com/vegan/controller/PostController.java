package com.vegan.controller;


import com.vegan.entity.Post;
import com.vegan.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class PostController {
    private final PostService postService;



    // 글 작성/수정 화면 열기
    @GetMapping("/post")
    public String showPostForm(@RequestParam(required = false) Integer id, Model model) {
        if (id != null) {
            Post post = postService.findById(id); // id로 기존 글 조회
            model.addAttribute("post", post); // 수정할 글 데이터를 모델에 담음
        }
        return "admin/post"; // 실제 JSP/HTML 뷰 이름
    }

    // 수정 화면
    @GetMapping("/post-edit")
    public String showEditForm(@RequestParam Integer id, Model model) {
        Post post = postService.findById(id);
        if (post == null) post = new Post(); // null 대비
        model.addAttribute("post", post);
        return "admin/post-edit"; // templates/admin/post-edit.html
    }
    //수정 처리
    @PostMapping("/post-edit")
    public String submitEdit(@ModelAttribute Post post) {
        postService.update(post); // DB에 저장
        return "redirect:/admin/post"; // 저장 후 목록으로
    }
   // 게시글 화면 열기
   @GetMapping("/post-view")
   public String viewPost(@RequestParam Long id, Model model) {
       System.out.println("getting post1");
       Post post = postService.findById(id);
       if (post == null) {
           throw new RuntimeException("게시글이 존재하지 않습니다.");
       }
       model.addAttribute("post", post);
       return "admin/post-view";
   }








}
