package com.bproject.course.controller;

import com.bproject.course.entity.Course;
import com.bproject.course.service.CourseService;
import com.bproject.user.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/course")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/upload-and-get-id")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadCourseAndGetId(
            @RequestParam("courses_name") String coursesName,
            @RequestParam("description") String description,
            @RequestParam("videoFile") MultipartFile videoFile,
            @RequestParam("thumbnailFile") MultipartFile thumbnailFile,
            @RequestParam("courses_category") Course.CourseCategory courses_category,
            HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");

        //  [핵심] 로그인 유저 검증 및 ID 추출
        if (loginUser == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "로그인 상태가 아닙니다. 다시 로그인 해주세요.");
            return ResponseEntity.status(401).body(errorResponse);
        }
        int instructorId = loginUser.getId(); // ⭐️ 로그인된 유저의 ID를 강사 ID로 사용

        try {
            // ⭐Service 메서드 호출 시 추출한 instructorId를 전달
            Course savedCourse = courseService.uploadCourse(
                    coursesName, description, videoFile, thumbnailFile, courses_category, instructorId
            );

            Map<String, Object> response = new HashMap<>();
            response.put("id", savedCourse.getId());
            response.put("message", "강의가 성공적으로 등록되었습니다.");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "강의 등록에 실패했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // 수정페이지 연결
    @GetMapping("/edit/{id}")
    public String editCourse(@PathVariable int id, Model model) {
        Course course = (Course) courseService.findById(id).orElseThrow(
                ()->new IllegalArgumentException("Invaild post id "));
        model.addAttribute("course",course);
        System.out.println("course : "+course);
        return "teacher/editCourse";
    }

    //강의 수정
    @PostMapping("/edit")
    public String updateCourse(@RequestParam("id") int id,
                               @RequestParam("courses_name") String coursesName,
                               @RequestParam("description") String description,
                               @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
                               @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
                               @RequestParam("courses_category") Course.CourseCategory courses_category,
                               @RequestParam(value = "existingVideoUrl", required = false) String existingVideoUrl,
                               @RequestParam(value = "existingThumbnailUrl", required = false) String existingThumbnailUrl,
                               RedirectAttributes redirectAttributes) {

        try {
            courseService.updateCourse(id, coursesName, description, videoFile, thumbnailFile, courses_category, existingVideoUrl, existingThumbnailUrl);
            redirectAttributes.addFlashAttribute("message", "강의가 성공적으로 수정되었습니다.");
            return "/teacher/myCourse";
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "강의 수정에 실패했습니다.");
            return "redirect:course/edit/" + id;
        }
    }

    // 인기 강의 클릭
    @GetMapping("/main")
    public String courseMain(@RequestParam(required = false) String searchKeyword, Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            model.addAttribute("message", "인기 강의를 보려면 로그인해야 합니다.");
            return "redirect:/login";
        }

        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            // 홈에서 전달받은 검색어를 모델에 담아 Thymeleaf로 전달
            model.addAttribute("initialKeyword", searchKeyword.trim());
        }


        return "courses/courses";
    }



}