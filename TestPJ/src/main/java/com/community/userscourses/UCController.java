package com.bproject.userscourses;

import com.bproject.course.entity.Course;
import com.bproject.course.service.CourseService;
import com.bproject.user.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class UCController {

    private final UCService ucService;
    private final CourseService courseService;

    @PostMapping("/{courseId}/progress")
    public ResponseEntity<?> updateVideoProgress(
            @PathVariable int courseId,
            @RequestBody Map<String, Integer> requestBody,
            HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Integer durationSec = requestBody.get("duration_sec");
        if (durationSec == null || durationSec < 0) {
            return ResponseEntity.badRequest().body("유효한 duration_sec 값이 필요합니다.");
        }

        Optional<Course> courseOpt = courseService.findById(courseId);
        if (courseOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("강의를 찾을 수 없습니다.");
        }

        Course course = courseOpt.get();
        int totalSec = course.getTotal_sec();

        if (totalSec <= 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("강의 총 시간(total_sec)이 0 이하입니다.");
        }

        // 1. (진행 길이 / 총 길이) * 100을 double로 계산
        double rawProgress = ((double) durationSec / totalSec) * 100;

        // 2. Math.round()로 반올림하여 정수로 변환
        int roundedProgress = (int) Math.round(rawProgress);

        // 3. Math.min을 사용하여 최대 100%를 초과하지 않도록 보장합니다.
        int progress = Math.min(100, roundedProgress);

        // ----------------------------------------------------------------------------------

        // 4. 진도율과 시청 시간(durationSec)을 함께 저장/업데이트
        ucService.saveOrUpdateProgress(loginUser.getId(), courseId, progress, durationSec);

        return ResponseEntity.ok().build();
    }
}
