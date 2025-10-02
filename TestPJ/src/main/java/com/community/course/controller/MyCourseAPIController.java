package com.bproject.course.controller;

import com.bproject.course.CoursePageDto;
import com.bproject.course.service.CourseService;
import com.bproject.user.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teacher")
public class MyCourseAPIController {

    private final CourseService courseService;

    public MyCourseAPIController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/courses")
    public ResponseEntity<CoursePageDto> getMyCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "전체") String category,
            HttpSession session) { // ⭐️ [추가] 세션을 받습니다.

        User loginUser = (User) session.getAttribute("loginUser");

        // 🚨 [핵심] 로그인 유저 검증
        if (loginUser == null) {
            // 로그인되지 않은 경우 401 Unauthorized 반환 (혹은 빈 목록 반환)
            return ResponseEntity.status(401).build();
        }
        int instructorId = loginUser.getId(); // 로그인된 유저 ID 추출

        // Service 메서드 호출 시 instructorId를 추가로 전달합니다.
        CoursePageDto coursePage = courseService.findCoursesByInstructor(
                instructorId, page, size, search, category
        );
        return ResponseEntity.ok(coursePage);
    }

    @DeleteMapping("/course/delete/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable int id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}