package com.bproject.course.controller;

import com.bproject.course.CourseDetailDto;
import com.bproject.course.entity.Course;
import com.bproject.course.service.CourseService;
import com.bproject.user.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseAPIController {

    private final CourseService courseService;


    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDetailDto> getCourseDetails(@PathVariable int courseId,
                                                             HttpSession session) {

        // 1. 강의 기본 정보 조회
        Course course = courseService.findById(courseId).orElse(null);

        if (course == null) {
            return ResponseEntity.notFound().build();
        }

        // 2. 유저 ID 및 시청 시간 조회
        int userDurationSec = 0;
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser != null) {
            int userId = loginUser.getId();

            // ⭐️ CourseService를 통해 유저가 마지막으로 시청한 시간(초)을 조회
            // 이전에 CourseService에 추가했던 getUserDurationSecForClient 메서드를 사용
            userDurationSec = courseService.getUserDurationSecForClient(userId, courseId);
        }

        // 3. DTO 생성 및 반환
        CourseDetailDto responseDto = new CourseDetailDto(course, userDurationSec);

        return ResponseEntity.ok(responseDto);
    }

    // 좋아요 수 업데이트 및 최신 좋아요 수 반환
    @PostMapping("/{courseId}/like")
    public ResponseEntity<Map<String, Integer>> updateLikeCount(@PathVariable int courseId) {
        int updatedCount = courseService.increaseLikeCount(courseId);
        Map<String, Integer> response = new HashMap<>();
        response.put("like_count", updatedCount);
        return ResponseEntity.ok(response);
    }

    // 좋아요 수 감소 및 최신 좋아요 수 반환
    @DeleteMapping("/{courseId}/like")
    public ResponseEntity<Map<String, Integer>> decreaseLikeCount(@PathVariable int courseId) {
        int updatedCount = courseService.decreaseLikeCount(courseId);
        Map<String, Integer> response = new HashMap<>();
        response.put("like_count", updatedCount);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public List<CourseDetailDto> getAllCourses(HttpSession session) { // ⭐️ 반환 타입과 인자 수정

        // 1. 로그인 유저 ID 확인 (세션 기반)
        User loginUser = (User) session.getAttribute("loginUser");
        int userId = (loginUser != null) ? loginUser.getId() : 0; // 비회원(userId=0)도 조회 가능하게 처리

        // 2. Service에 userId를 전달하여 진도율이 포함된 DTO 목록을 받음
        return courseService.findAllCoursesWithProgress(userId); // ⭐️ 메서드명 변경 (아래 Service에서 구현)
    }



}