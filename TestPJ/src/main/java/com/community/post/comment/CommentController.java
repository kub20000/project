package com.community.post.comment;

import com.community.post.entity.Post;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    // 댓글 등록
    @PostMapping("/comment/add")
    public String addPost(@ModelAttribute Comment comment, @RequestParam int postId) {
        comment.setPosts_id(postId);
        commentService.add(comment);
        return "redirect:/post/detail/"+postId;
    }

    // 댓글 수정
//    @PostMapping("/comment/edit")
//    public String userEdit(@ModelAttribute Comment comment, RedirectAttributes redirectAttributes, @RequestParam int postId){
//        comment.setPosts_id(postId);
//        commentService.update(comment);
//        redirectAttributes.addAttribute("id", comment.getId());
//        return "redirect:/post/detail/"+postId;
//    }




}
