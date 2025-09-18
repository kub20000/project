package com.community.course.quiz;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    private final QuizRepository quizRepository;

    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    public Optional<Quiz> findById(long id) {
        return quizRepository.findById(id);
    }

    public int getTotalQuizzesForCourse(Quiz quiz) {
        if (quiz != null) {
            List<Quiz> quizzes = quizRepository.findByCourses_id(quiz.getCourses_id());
            return quizzes.size();
        }
        return 0;
    }

    // Quiz 객체를 받아 이전/다음 퀴즈 ID를 찾습니다.
    public QuizNavigation findNavIds(Quiz currentQuiz) {
        if (currentQuiz == null) {
            return new QuizNavigation(null, null);
        }

        Long courses_id = currentQuiz.getCourses_id();

        Optional<Quiz> prevQuiz = quizRepository.findFirstByIdLessThanAndCourses_idOrderByIdDesc(currentQuiz.getId(), courses_id);
        Optional<Quiz> nextQuiz = quizRepository.findFirstByIdGreaterThanAndCourses_idOrderByIdAsc(currentQuiz.getId(), courses_id);

        Long prevId = prevQuiz.map(Quiz::getId).orElse(null);
        Long nextId = nextQuiz.map(Quiz::getId).orElse(null);

        return new QuizNavigation(prevId, nextId);
    }

    // 네비게이션 ID를 담을 DTO 클래스
    public static class QuizNavigation {
        private final Long prevId;
        private final Long nextId;

        public QuizNavigation(Long prevId, Long nextId) {
            this.prevId = prevId;
            this.nextId = nextId;
        }

        public Long getPrevId() { return prevId; }
        public Long getNextId() { return nextId; }
    }

    // 강의 id로 퀴즈 연결
    public Optional<Quiz> findFirstByCourseId(Long courseId) {
        return quizRepository.findFirstByCourses_idOrderByIdAsc(courseId);
    }




}
