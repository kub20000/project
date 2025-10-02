package com.bproject.mypage;

import com.bproject.post.PostPageDto;
import com.bproject.user.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class myPageAPIController {

    private final myPageService myPageService;

    // 내 게시글 관리 목록 API
    @GetMapping("/posts")
    public ResponseEntity<?> getMyPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, // 페이지 사이즈는 HTML과 맞춥니다.
            @RequestParam(defaultValue = "") String keyword,
            HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        String authorNickname = loginUser.getNickname(); // 로그인 유저의 닉네임 추출

        try {
            PostPageDto postPage = myPageService.findMyPosts(authorNickname, page, size, keyword);
            return ResponseEntity.ok(postPage);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "게시글 조회에 실패했습니다."));
        }
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deleteMyPost(@PathVariable int postId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401).build(); // 401 Unauthorized
        }

        String authorNickname = loginUser.getNickname();

        // MyPageService의 권한 체크 로직 호출
        boolean success = myPageService.deletePost(postId, authorNickname);

        if (success) {
            // 204 No Content (성공적으로 처리됨)
            return ResponseEntity.noContent().build();
        } else {
            // 403 Forbidden (작성자가 아님)
            return ResponseEntity.status(403).build();
        }

    }

}