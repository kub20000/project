// src/main/java/com/bproject/web/ViewController.java
package com.bproject.web;

import com.bproject.course.entity.Course;
import com.bproject.course.service.CourseService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final CourseService courseService;


//    // 로그인전 홈 화면
//    @GetMapping("/")
//    public String home() {
//        return "home";  // templates/user/index.html
//    }

    @GetMapping("/home")
    public String returnhome(Model model){

        // 1. 인기 강의 목록 조회 (좋아요 순 상위 3개)
        List<Course> topCourses = courseService.getTop3PopularCourses();
        model.addAttribute("topCourses", topCourses);

        return "home";
    }

//    @GetMapping("/mainhome")
//    public String mainhome() {
//        return "mainhome"; // templates/home.html
//    }

    @GetMapping("/myFridge")
    public String myFridge() {
        return "myFridge/myFridge";
    }





    @GetMapping("courses")
    public String course(HttpSession session) {

        if (session.getAttribute("loginUser") == null) {
            // 미로그인 시 로그인 페이지로 리다이렉트
            return "redirect:/login";
        }

        return "/courses/courses";
    }

//    @GetMapping("post/main")
//    public String postMain(){
//        return "redirect:/post/list";
//    }


    @GetMapping("/myStudy")
    public String myStudy() {
        return "mypage/myStudy";
    }
    @GetMapping("/myRecipe")
    public String myRecipe() {
        return "mypage/myRecipe";
    }
    @GetMapping("/myBoard")
    public String myBaord() {
        return "mypage/myBoard";
    }


}
