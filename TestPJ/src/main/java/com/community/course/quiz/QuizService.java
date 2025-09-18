package com.community.course.quiz;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuizService {

    private final QuizRepo quizRepo;

    public QuizService(QuizRepo quizRepo) {
        this.quizRepo = quizRepo;
    }

    public Optional<Quiz> findById(long id) {
        return quizRepo.findById(id);
    }


}
