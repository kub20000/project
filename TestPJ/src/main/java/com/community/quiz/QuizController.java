package com.bproject.quiz;

import com.bproject.course.entity.Course;
import com.bproject.course.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;
    private final CourseService courseService;

    public QuizController(QuizService quizService, CourseService courseService) {
        this.quizService = quizService;
        this.courseService = courseService;
    }

    @GetMapping("/{coursesId}")
    public String quizListForm(@PathVariable long coursesId, Model model) {
        model.addAttribute("quizzes", quizService.findAllByCoursesId(coursesId));
        return "course/quiz";
    }

    @PostMapping("/submit")
    public String submitQuiz(@ModelAttribute QuizListWrapper quizListWrapper, Model model) {
        QuizResult result = quizService.processQuizSubmission(quizListWrapper.getQuizzes());

        model.addAttribute("correctCount", result.getCorrectCount());
        model.addAttribute("totalCount", result.getTotalCount());
        model.addAttribute("incorrectQuizzes", result.getIncorrectQuizzes());

        return "/course/quizResult"; // quizResult.html 템플릿 반환
    }

    @GetMapping("/upload/{courseId}")
    public String showQuizUploadForm(@PathVariable long courseId, Model model) {
        Course course = courseService.findById((int) courseId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course ID"));
        model.addAttribute("course", course);
        return "teacher/uploadQuiz";
    }

    @PostMapping("/upload")
    @ResponseBody // 응답 본문을 직접 반환하도록 설정
    public ResponseEntity<Map<String, String>> uploadQuiz(@RequestBody List<Quiz> quizzes) {
        try {
            // 여러 개의 퀴즈를 순회하며 서비스에 전달
            for (Quiz quiz : quizzes) {
                quizService.uploadQuiz(
                        quiz.getCoursesId(),
                        quiz.getQuiz_name(),
                        quiz.getQuiz_question(),
                        quiz.getQuiz_result()
                );
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "퀴즈가 성공적으로 등록되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "퀴즈 등록에 실패했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    //퀴즈 수정 페이지 연결
    @GetMapping("/edit/{coursesId}")
    public String showEditQuizForm(@PathVariable long coursesId, Model model) {
        Course course = courseService.findById((int) coursesId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course ID"));
        model.addAttribute("course", course);
        return "teacher/editQuiz";
    }

    // 퀴즈 목록을 JSON으로 반환 (HTML의 fetch 요청용)
    @GetMapping("/list/{coursesId}")
    @ResponseBody
    public List<Quiz> getQuizzesByCoursesId(@PathVariable long coursesId) {
        return quizService.findAllByCoursesId(coursesId);
    }

    // 퀴즈 수정
    @PostMapping("/edit")
    @ResponseBody
    public ResponseEntity<Map<String, String>> editQuiz(@RequestBody List<Quiz> quizzes) {
        try {
            quizService.updateQuizzes(quizzes);
            Map<String, String> response = new HashMap<>();
            response.put("message", "퀴즈가 성공적으로 수정되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "퀴즈 수정에 실패했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

}