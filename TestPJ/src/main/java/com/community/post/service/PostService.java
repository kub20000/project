package com.community.post.service;

import com.community.post.entity.Post;
import com.community.post.repository.PostRepo;
import org.springframework.stereotype.Service;

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
}
