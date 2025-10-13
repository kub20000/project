package com.bproject.user.controller;

import com.bproject.user.entity.Post;
import com.bproject.user.service.AdminPostService;
import com.bproject.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api") // API 전용 경로
public class AdminPostAPIController {
    private final AdminPostService postService;
    private final UserService userService;

    public AdminPostAPIController(AdminPostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    // 게시글 단일 조회 (REST API)
    @GetMapping("/post/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        System.out.println("post edit****"+id+id);
        Post post = postService.findById(id);
        if (post == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(post);
    }


    // 2️⃣ 게시글 수정 저장
    @PutMapping("/post/{id}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long id,
            @RequestBody Post postData) {
        System.out.println("postedit***"+id);
        System.out.println("Received id: " + id);
        System.out.println("Received postData: title=" + postData.getTitle() + ", content=" + postData.getContent());

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
        response.put("posts", pagePosts); // 필수
        response.put("totalPages", totalPages);

        return ResponseEntity.ok(response);
    }
    // 자유게시판 삭제
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        System.out.println("deletePost" + id);
        try {
            postService.deletePost(id);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
        }
    }

}



