package com.bproject.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
        // 1. 사용자 답안 DB에 저장
        quizRepo.saveUserAnswers(submittedQuizzes);

        // 2. 제출된 퀴즈 ID 목록 추출
        List<Long> quizIds = new ArrayList<>();
        for (Quiz quiz : submittedQuizzes) {
            quizIds.add(quiz.getId());
        }

        // 3. 정답 데이터 DB에서 불러오기
        List<Quiz> correctAnswers = quizRepo.findQuizzesByIds(quizIds);

        // 4. 채점 로직
        int correctCount = 0;
        List<Quiz> incorrectQuizzes = new ArrayList<>();

        for (Quiz submitted : submittedQuizzes) {
            // 제출된 퀴즈와 일치하는 정답 찾기
            Quiz correct = correctAnswers.stream()
                    .filter(q -> q.getId() == submitted.getId())
                    .findFirst()
                    .orElse(null);

            if (correct != null) {
                if (submitted.getQuiz_answer().equalsIgnoreCase(correct.getQuiz_result())) {
                    correctCount++;
                } else {
                    // 오답인 경우, 오답 목록에 추가
                    incorrectQuizzes.add(correct);
                }
            }
        }

        // 5. 결과 객체 반환
        return new QuizResult(correctCount, submittedQuizzes.size(), incorrectQuizzes);
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