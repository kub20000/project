package com.bproject;

import com.bproject.course.entity.Course;
import com.bproject.course.service.CourseService;
import com.bproject.user.entity.User;
import jakarta.servlet.http.HttpSession;
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

    @GetMapping("/mainhome")
    public String root(HttpSession session, Model model) {

        //  인기 강의 목록 조회 (좋아요 순 상위 3개)
        List<Course> topCourses = courseService.getTop3PopularCourses();
        model.addAttribute("topCourses", topCourses);


        // 세션에서 로그인한 사용자 정보 가져오기
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            // 로그인 안 되어 있으면 로그인 페이지로
            return "redirect:/login";
        }

        // Role에 따라 리다이렉트
//        switch (loginUser.getRole()) {
//            case ADMIN:
//                return "redirect:/admin/indexmg";       // 관리자 홈
//            case USER:
//                return "redirect:/mainhome";          // 일반 사용자 홈
//            case INSTRUCTOR:
//                return "redirect:/teacher/homeT";   // 강사 홈
//            default:
//                return "redirect:/error";               // 알 수 없는 권한
//        }
        switch (loginUser.getRole()) {
            case ADMIN:
                return "admin/indexmg";       // 관리자 홈
            case USER:
                return "mainhome";          // 일반 사용자 홈
            case INSTRUCTOR:
                return "teacher/homeT";   // 강사 홈
            default:
                return "error";               // 알 수 없는 권한
        }

    }





}
