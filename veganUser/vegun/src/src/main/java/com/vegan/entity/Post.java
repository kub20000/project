package com.vegan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "post")
public class Post {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private long id;
    private String author;
    private String title;
    private String content;

    private LocalDateTime created_at; // 작성일

    @Enumerated(EnumType.STRING) // enum 저장 시 문자열로 DB에 저장
    private Locale.Category category;

    private Boolean fixed; // 공지 고정 여부
    private Object updatedAt;

    public void setCreatedAt(LocalDateTime now) {

        this.updatedAt = updatedAt;
    }

    public Object isFixed() {
        return this.fixed;
    }

    public Object getCreatedAt() {
        return this.created_at;
    }
}