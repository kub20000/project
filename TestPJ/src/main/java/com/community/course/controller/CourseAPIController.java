package com.community.course.controller;

import com.community.course.entity.Course;
import com.community.course.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
public class CourseAPIController {

    private final CourseService courseService;

    public CourseAPIController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/{courseId}")
    public Optional<Course> getCourseDetails(@PathVariable int courseId) {
        // findById 메서드에서 Course 객체를 직접 받아 반환
        return courseService.findById(courseId);
    }

    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.findAll();
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

    // 진도율 업데이트 API 추가
    @PostMapping("/{courseId}/progress")
    public ResponseEntity<Void> updateProgress(@PathVariable int courseId, @RequestBody Map<String, Integer> payload) {
        int durationSec = payload.get("duration_sec");
        courseService.updateProgress(courseId, durationSec);
        return ResponseEntity.ok().build();
    }


}
