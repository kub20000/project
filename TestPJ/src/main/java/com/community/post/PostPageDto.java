package com.bproject.post;

import com.bproject.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostPageDto {
    private List<Post> content;     // 현재 페이지의 게시글 목록
    private int currentPage;        // 현재 페이지 번호 (0부터 시작)
    private int totalPages;         // 총 페이지 수
    private long totalItems;        // 총 게시글 개수
}
