package com.vegan.service;

import com.vegan.entity.Post;
import com.vegan.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepo;

    //id 찾기
    public Post findById(long id) {
        return postRepo.findById(id);
    }
    //전체조회
    public List<Post> findAll() {

        return postRepo.findAll();
    }
     //수정
    public void update(Post post) {
        postRepo.update(post);
    }
     //추가
    public void addPost(Post post) {
        postRepo.save(post);
    }




}
