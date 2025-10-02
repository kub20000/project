package com.bproject.mypage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressDto {
    private String category; // 카테고리 명 (LIFE, SKILL, RECIPE)
    private int rate;        // 진도율 (0 ~ 100)
}
