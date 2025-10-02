package com.bproject.post.repository;

import com.bproject.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Integer> {

    // 제목으로만 검색
    Page<Post> findByTitleContaining(String title, Pageable pageable);

    // 내용으로만 검색
    Page<Post> findByContentContaining(String content, Pageable pageable);

    // 작성자로만 검색
    Page<Post> findByAuthorContaining(String author, Pageable pageable);

    // 페이지네이션을 포함한 전체 목록
    Page<Post> findAll(Pageable pageable);

    List<Post> findByFixedTrue();

    Page<Post> findByFixedFalse(Pageable pageable);

    // 닉네임으로만 필터링된 게시글 총 개수 카운트
    long countByAuthorAndTitleContaining(String author, String title);
}