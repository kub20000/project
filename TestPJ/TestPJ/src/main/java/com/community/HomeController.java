package com.community;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // /요청을 처리한다.  /=root
    @GetMapping("/")
    public String home(){
        return "redirect:/post/list";
    }

}
