package com.vegan.service;

import com.vegan.entity.Post;
import com.vegan.entity.User;
import com.vegan.repository.AdminPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminPostService {
    private final AdminPostRepository postRepo;

     // 게시글 추가
    public Post add(Post post, User loginUser) {
        post.setCreated_at(LocalDateTime.now()); // 작성일
        return postRepo.save(post,loginUser);        // DB 저장
    }

    // 게시글 수정
    public Post update(Post post) {
        return postRepo.update(post);
    }

    // 단일 게시글 조회
    // PostService.java
    public Post findById(Long id) {
        return postRepo.findById(id);
    }

    // 전체 게시글 조회
    public List<Post> getAllPosts() {
        return postRepo.findAll();
    }

    // 게시글 삭제
    public void delete(Long id) {
        postRepo.deleteById(id); // Repository에서 삭제 실행
    }

    public Post save(Post post, User loginUser) {
        post.setCreated_at(LocalDateTime.now());
        return postRepo.save(post,loginUser);
    }
    // 자유게시판 게시글 삭제
    public void deletePost(Long id) {
        postRepo.deleteById(id);
    }

}
