package com.community.course.quiz;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/api/quiz/{quizId}")
    public String quizForm(@PathVariable long quizId, Model model) {
        Quiz quiz = (Quiz) quizService.findById(quizId).orElseThrow(
                ()->new IllegalArgumentException("Invaild quiz"));
        model.addAttribute("quiz",quiz);
        return "course/quiz";
    }

}
