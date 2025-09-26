package com.community.course.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResult {
    private int correctCount;
    private int totalCount;
    private List<Quiz> incorrectQuizzes;
    private List<String> userIncorrectAnswers;
}
