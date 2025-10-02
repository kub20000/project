package com.vegan.controller;


import com.vegan.entity.Post;
import com.vegan.entity.User;
import com.vegan.service.AdminPostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/") // 클래스 단위 API 경로
public class AdminPostAPIController {
    private final AdminPostService postService;

    public AdminPostAPIController(AdminPostService postService) {
        this.postService = postService;
    }

    // 게시판 불러오기
    // 모든 게시글 가져오기
    @GetMapping("/post-list")
    public ResponseEntity<List<Map<String, Object>>> getPostList() {
        List<Post> posts = postService.getAllPosts();

        List<Map<String, Object>> result = posts.stream().map(post -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", post.getId());
            map.put("title", post.getTitle());
            map.put("author", post.getAuthor() != null ? post.getAuthor() : "Unknown");
            map.put("created_at", post.getCreatedAt() != null ? post.getCreatedAt().toString() : "");
            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }
    //공지사항 수정
    @PutMapping("/post/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody Map<String, Object> postData) {
        System.out.println("post"+id);
        Post post = postService.findById(id);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글 없음");
        }

        // 제목, 내용 업데이트
        post.setTitle((String) postData.get("title"));
        post.setContent((String) postData.get("content"));

        // DB 저장
        postService.save(post);

        return ResponseEntity.ok(post);
    }
}

























