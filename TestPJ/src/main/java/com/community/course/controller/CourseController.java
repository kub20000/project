package com.community.course.controller;

import com.community.course.entity.Course;
import com.community.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class CourseController {

    @Autowired
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/course/upload")
    public String uploadCourse(@RequestParam("courses_name") String coursesName,
                               @RequestParam("description") String description,
                               @RequestParam("videoFile") MultipartFile videoFile,
                               @RequestParam("thumbnailFile") MultipartFile thumbnailFile,
                               @RequestParam("courses_category") Course.CourseCategory courses_category) {

        try {
            // CourseService를 사용하여 파일 저장 및 데이터베이스에 Course 객체 저장
            courseService.uploadCourse(coursesName, description, videoFile, thumbnailFile, courses_category);

            return "redirect:/course/main"; // 업로드 성공 후 이동할 페이지
        } catch (IOException e) {
            e.printStackTrace();
            // 업로드 실패 시 에러 페이지로 이동
            return "error/uploadError";
        }
    }

}
