package com.vegan.service;

import com.vegan.entity.Post;
import com.vegan.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepo;

     // 게시글 추가
    public Post add(Post post, String author) {
        post.setAuthor(author);                 // 작성자
        post.setCreated_at(LocalDateTime.now()); // 작성일
        return postRepo.save(post);        // DB 저장
    }

    // 게시글 수정
    public Post update(Post post) {
        return postRepo.update(post);
    }

    // 단일 게시글 조회
    public Post findById(Long id) {
        return postRepo.findById(id); // null 가능
    }

    // 전체 게시글 조회
    public List<Post> findAll() {
        return postRepo.findAll();
    }

    // 게시글 삭제
    public void delete(Long id) {
        postRepo.deleteById(id); // Repository에서 삭제 실행
    }

}
