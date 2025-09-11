package com.community.post.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Comment {
    private int id;
    private int posts_id;
    private String comments_name;
    private String comments_content;
    private LocalDateTime created_at;
}
