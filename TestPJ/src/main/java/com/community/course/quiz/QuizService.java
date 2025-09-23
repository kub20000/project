package com.community.course.quiz;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    private final QuizRepo quizRepo;

    public QuizService(QuizRepo quizRepo) {
        this.quizRepo = quizRepo;
    }

    public List<Quiz> findAllByCoursesId(long coursesId) {
        return quizRepo.findByCoursesId(coursesId);
    }

    public Optional<Quiz> findOneByCoursesId(long coursesId) {
        return quizRepo.findOneByCoursesId(coursesId);
    }
}
