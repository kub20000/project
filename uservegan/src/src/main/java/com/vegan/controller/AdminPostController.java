package com.vegan.controller;


import com.vegan.entity.Post;
import com.vegan.entity.User;
import com.vegan.service.AdminPostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminPostController {
    private final AdminPostService postService;

    //게시판 열기
    @GetMapping("/post")
    public String postList() {
        return "admin/post";  // post.html 렌더링
    }
    // GET: 게시글 수정 화면
    @GetMapping("/post-edit/{id}")
    public String editPostForm(@PathVariable Long id, Model model) {
        System.out.println("post edit");
        Post post = postService.findById(id);
        if (post == null) {
            return "redirect:/admin/post";
        }
        model.addAttribute("post", post);
        return "admin/post-edit"; // Thymeleaf form 화면
    }

    // POST: 게시글 저장
    @PostMapping("/post-edit")
    public String updatePost(@ModelAttribute Post post) {
        System.out.println("postup");
        postService.save(post); // 수정 저장
        return "redirect:/admin/post"; // 저장 후 목록
    }





}




