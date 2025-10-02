package com.bproject.post.comment;

import com.bproject.post.entity.Post;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public void add(Comment comment) {
        System.out.println("comment add");
        if (comment.getCreated_at() == null) {
            comment.setCreated_at(LocalDateTime.now());
        }
        commentRepo.add(comment);
    }

    public void update(Comment comment) {
        System.out.println("comment update");
        commentRepo.update(comment);
    }

    public void deleteByPostsId(int postsId) {
        commentRepo.deleteByPostsId(postsId);
    }
}