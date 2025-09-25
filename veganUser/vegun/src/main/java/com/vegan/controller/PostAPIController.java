package com.vegan.controller;


import com.vegan.entity.Post;
import com.vegan.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @GetMapping("post-edit/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        Post post = postService.findById(id);
        if (post == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(post);
    }


     // 2️⃣ 게시글 수정 저장
    @PutMapping("post-edit/{id}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long id,
            @RequestBody Post postData) {

        // 경로 id로 강제 지정 (보안/일관성)
        postData.setId(id);

        try {
            Post updated = postService.update(postData); // DB 업데이트
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            // 게시글이 없거나 DB 제약조건 위반 시
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


}



