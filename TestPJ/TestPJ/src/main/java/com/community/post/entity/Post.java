package com.community.post.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Post {
    // 게시글 변수 값
    private int id;
    private String author;
    private String title;
    private String content;
    private LocalDateTime created_at;
}