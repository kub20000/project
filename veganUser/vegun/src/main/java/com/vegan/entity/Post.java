package com.vegan.entity;

import com.vegan.repository.AdminPostRepository;
import com.vegan.service.AdminPostService;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "posts") // DB 테이블 이름과 일치
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String author;
    private String title;
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private Locale.Category category;

    private Boolean fixed;

    public void setCreated_at(LocalDateTime now) {
        this.createdAt = now;
    }

}


