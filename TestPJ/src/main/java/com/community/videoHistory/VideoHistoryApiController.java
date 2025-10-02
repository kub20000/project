package com.bproject.videoHistory;

import com.bproject.user.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/video-history")
@RequiredArgsConstructor
public class VideoHistoryApiController {

    private final VHService vhService;

    @Data
    public static class HistoryRequest {
        private int videoId;
        private int progressRate;
    }

    @PostMapping
    public ResponseEntity<?> saveHistory(
            @RequestBody HistoryRequest request,
            HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 💡 VHService에 최종 시청 기록을 DB에 저장하는 메서드를 호출해야 합니다.
        // 이 메서드는 watched_date 컬럼에 CURRENT_TIMESTAMP를 기록해야 합니다.
        vhService.saveNewHistory(loginUser.getId(), request.getVideoId(), request.getProgressRate());

        return ResponseEntity.ok().build();
    }
}