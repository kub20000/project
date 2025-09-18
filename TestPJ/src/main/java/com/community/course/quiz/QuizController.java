package com.community.course.quiz;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/quiz/{courseId}")
    public String quizForm(@PathVariable long courseId,
                           @RequestParam(name = "quizId", required = false) Long quizId,
                           Model model) {

        long currentQuizId;
        if (quizId != null) {
            currentQuizId = quizId;
        } else {
            Optional<Quiz> firstQuiz = quizService.findFirstByCourseId(courseId);
            if (firstQuiz.isPresent()) {
                currentQuizId = firstQuiz.get().getId();
            } else {
                return "error/no-quiz-found";
            }
        }

        Quiz quiz = quizService.findById(currentQuizId).orElseThrow(
                () -> new IllegalArgumentException("Invalid quiz ID: " + currentQuizId));

        int totalQuizzes = quizService.getTotalQuizzesForCourse(quiz);
        QuizService.QuizNavigation nav = quizService.findNavIds(quiz);

        model.addAttribute("quiz", quiz);
        model.addAttribute("totalQuizzes", totalQuizzes);
        model.addAttribute("prevId", nav.getPrevId());
        model.addAttribute("nextId", nav.getNextId());

        return "course/quiz";
    }



}
