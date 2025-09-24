package com.community.course.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
