package com.bproject.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {
    private long id;
    private long coursesId;
    private String quiz_name;
    private String quiz_question;
    private String quiz_answer;
    private String quiz_result;
}