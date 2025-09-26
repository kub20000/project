package com.community;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // /요청을 처리한다.  /=root
    @GetMapping("/post/main")
    public String postMain(){
        return "redirect:/post/list";
    }

    @GetMapping("/")
    public String home(){
        return "home";
    }

    @GetMapping("/course/main")
    public String course() {return "/course/courses";}

    @GetMapping("/myFridge/main")
    public String myFridge() {return "/myFridge/myFridge";}

    @GetMapping("/teacher/uploadCourse")
    public String uploadCourse() {return "/teacher/uploadCourse";}

    @GetMapping("/teacher/port")
    public String port() {return "/teacher/port";}

    @GetMapping("/teacher/myCourse")
    public String myCourse() {return "/teacher/myCourse";}
}
