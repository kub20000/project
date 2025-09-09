package com.community.post.service;

import com.community.post.entity.Post;
import com.community.post.repository.PostRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private final PostRepo postRepo;

    public PostService(PostRepo postRepo) {
        this.postRepo = postRepo;
    }

    // 모두 단순 연결
    public List<Post> findAll() {
        return postRepo.findAll();
    }

    public Optional<Post> findById(int id) {
        return postRepo.findById(id);
    }

    public void add(Post post) {
        System.out.println("post add");
        if (post.getCreated_at() == null) {
            post.setCreated_at(LocalDateTime.now());
        }
        postRepo.add(post);
    }

    public void deleteById(int id) {
        System.out.println("post delete");
        postRepo.deleteById(id);
    }

    public void update(Post post) {
        System.out.println("post update");
        postRepo.update(post);
    }

    // 다음글 -> 현재 id보다 큰 id 중 가장 작은 id를 가져옴
    public Optional<Integer> findNextPostId(int postId) {
        return postRepo.findNextId(postId);
    }

    // 이전글 -> 현재 id보다 작은 id 중 가장 큰 id를 가져옴
    public Optional<Integer> findPrevPostId(int postId) {
        return postRepo.findPrevId(postId);
    }

}
