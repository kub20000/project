package com.community.post.comment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CommentController {

    private final CommentService commentService ;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 댓글 삭제
    @PostMapping("/comment/delete")
    public String deleteComment(@RequestParam int id, @RequestParam int posts_id) {
        commentService.deleteById(id);
        return "redirect:/post/detail/"+posts_id;
    }




}
