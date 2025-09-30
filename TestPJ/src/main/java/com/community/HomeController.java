package com.community;

import com.community.course.entity.Course;
import com.community.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CourseService courseService;

    // /요청을 처리한다.  /=root
    @GetMapping("/post/main")
    public String postMain(){
        return "redirect:/post/list";
    }

    // 홈 페이지 구성
    @GetMapping("/")
    public String home(Model model){

        // 1. 인기 강의 목록 조회 (좋아요 순 상위 3개)
        List<Course> topCourses = courseService.getTop3PopularCourses();
        model.addAttribute("topCourses", topCourses);

        return "home";
    }

    @GetMapping("/myFridge/main")
    public String myFridge() {return "/myFridge/myFridge";}

    @GetMapping("/teacher/uploadCourse")
    public String uploadCourse() {return "/teacher/uploadCourse";}

    @GetMapping("/teacher/port")
    public String port() {return "/teacher/port";}

    @GetMapping("/teacher/myCourse")
    public String myCourse() {return "/teacher/myCourse";}

    @GetMapping("/myPage/main")
    public String myPage() {return "/myPage/dashBoard";}
}
