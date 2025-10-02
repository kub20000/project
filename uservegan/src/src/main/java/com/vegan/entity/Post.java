package com.vegan.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    private Long id;                  // posts 테이블 PK
    private String author;            // 작성자 (username)
    private String title;             // 게시글 제목
    private String content;           // 내용
    private LocalDateTime createdAt;  // 생성일시
    private Category category;        // Enum 타입
    private int fixed = 0;// 고정 여부

    public enum Category {
        NOTICE, FREEBOARD
    }
}

