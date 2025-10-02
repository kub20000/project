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

    //전체 게시글 조회
    public List<Post> getAllPosts() {
        return postRepo.findAll();
    }
    //아이디로 조회
    public Post findById(Long id) {
        return postRepo.findById(id);
    }
    //저장
    public void save(Post post) {
        postRepo.save(post);

    }
}
