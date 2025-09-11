package com.community.post.comment;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepo commentRepo;

    public CommentService(CommentRepo commentRepo) {this.commentRepo = commentRepo;}

        public List<Comment> findById(int id) {
            return commentRepo.findById(id);
    }


    public void deleteById(int id) {
        System.out.println("comment delete");
        commentRepo.deleteById(id);
    }
}
