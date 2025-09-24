package com.community.course.quiz;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/quiz/{coursesId}")
    public String quizListForm(@PathVariable long coursesId, Model model) {
        model.addAttribute("quizzes", quizService.findAllByCoursesId(coursesId));
        return "course/quiz";
    }

}