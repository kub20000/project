package com.community.post.repository;

import com.community.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
}