package com.bproject.post.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table("posts")
public class Post {

    public enum Category {
        NOTICE, FREEBOARD
    }

    // 게시글 변수 값
    private int id;
    private String author;
    private String title;
    private String content;
    private LocalDateTime created_at;
    private Category category;
    private Boolean fixed;
}