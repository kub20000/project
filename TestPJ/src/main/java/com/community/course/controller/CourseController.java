package com.community.course.controller;

import com.community.course.entity.Course;
import com.community.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
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



}
