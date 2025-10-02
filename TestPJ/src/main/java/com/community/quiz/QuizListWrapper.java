package com.bproject.quiz;

import lombok.Data;

import java.util.List;

@Data
public class QuizListWrapper {
    private List<Quiz> quizzes;
}