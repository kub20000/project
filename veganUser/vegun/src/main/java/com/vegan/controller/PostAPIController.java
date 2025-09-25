package com.vegan.controller;


import com.vegan.entity.Post;
import com.vegan.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api") // API 전용 경로
public class PostAPIController {
    private final PostService postService;

    public PostAPIController(PostService postService) {
        this.postService = postService;
    }

    // 게시글 단일 조회 (REST API)
    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        System.out.println("getting post");
        Post post = postService.findById(id);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(post);
    }
}



