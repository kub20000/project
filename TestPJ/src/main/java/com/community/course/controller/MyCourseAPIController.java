package com.community.course.controller;

import com.community.course.CoursePageDto;
import com.community.course.service.CourseService;
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
            @RequestParam(defaultValue = "전체") String category) {

        CoursePageDto coursePage = courseService.findCoursesWithFilterAndPagination(page, size, search, category);
        return ResponseEntity.ok(coursePage);
    }

    @DeleteMapping("/course/delete/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable int id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
