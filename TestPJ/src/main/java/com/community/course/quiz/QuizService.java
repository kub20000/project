package com.community.course.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private final QuizRepo quizRepo;

    public QuizService(QuizRepo quizRepo) {
        this.quizRepo = quizRepo;
    }

    public List<Quiz> findAllByCoursesId(long coursesId) {
        return quizRepo.findByCoursesId(coursesId);
    }

    @Transactional
    public QuizResult processQuizSubmission(List<Quiz> submittedQuizzes) {
        quizRepo.saveUserAnswers(submittedQuizzes);

        List<Long> quizIds = submittedQuizzes.stream()
                .map(Quiz::getId)
                .collect(Collectors.toList());

        List<Quiz> correctAnswers = quizRepo.findQuizzesByIds(quizIds);

        int correctCount = 0;
        List<Quiz> incorrectQuizzes = new ArrayList<>();
        List<String> userIncorrectAnswers = new ArrayList<>(); // 추가된 부분

        for (Quiz submitted : submittedQuizzes) {
            Quiz correct = correctAnswers.stream()
                    .filter(q -> q.getId() == submitted.getId())
                    .findFirst()
                    .orElse(null);

            if (correct != null) {
                String cleanedSubmittedAnswer = submitted.getQuiz_answer() != null ? submitted.getQuiz_answer().replaceAll("\\s", "") : "";
                String cleanedCorrectAnswer = correct.getQuiz_result() != null ? correct.getQuiz_result().replaceAll("\\s", "") : "";

                if (cleanedSubmittedAnswer.equalsIgnoreCase(cleanedCorrectAnswer)) {
                    correctCount++;
                } else {
                    incorrectQuizzes.add(correct);
                    userIncorrectAnswers.add(submitted.getQuiz_answer()); // 추가된 부분: 사용자의 오답 답안 저장
                }
            }
        }
        return new QuizResult(correctCount, submittedQuizzes.size(), incorrectQuizzes, userIncorrectAnswers); // 수정된 부분
    }

    public long uploadQuiz(long coursesId, String quizName, String quizQuestion, String quizResult) {
        // Quiz 객체 생성
        Quiz quiz = new Quiz();
        quiz.setCoursesId(coursesId);
        quiz.setQuiz_name(quizName);
        quiz.setQuiz_question(quizQuestion);
        quiz.setQuiz_result(quizResult);

        // 레포지토리를 통해 데이터베이스에 저장하고, 저장된 ID를 반환
        return quizRepo.save(quiz);
    }

    //퀴즈 수정
    @Transactional
    public void updateQuizzes(List<Quiz> quizzes) {
        quizRepo.updateQuizzes(quizzes);
    }



}
