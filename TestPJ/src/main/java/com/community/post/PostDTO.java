package com.bproject.post;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PostDTO {
    private String author;
    private String title;
    private String content;
    private String searchType;
    private String keyword;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

}