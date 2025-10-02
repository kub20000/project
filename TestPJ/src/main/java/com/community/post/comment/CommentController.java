package com.bproject.post.comment;

import com.bproject.post.entity.Post;
import com.bproject.user.entity.User;
import jakarta.servlet.http.HttpSession;
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
    public String addComment(
            @ModelAttribute Comment comment,
            @RequestParam int postId,
            HttpSession session) { // ⭐️ HttpSession 추가

        // 1. 세션에서 로그인 유저 정보 가져오기
        User loginUser = (User) session.getAttribute("loginUser");

        // 2. 로그인 유저가 없을 경우 처리 (필요시 로그인 페이지 리다이렉트 등 로직 추가)
        if (loginUser == null) {
            System.out.println("ERROR: 댓글 작성 시 로그인된 유저 정보가 없습니다.");
            // 로그인 페이지로 리다이렉트하거나 오류 처리
            return "redirect:/login";
        }

        // 3. Comment 엔티티의 comments_name 필드에 로그인 유저의 nickname 설정 (핵심 로직)
        comment.setComments_name(loginUser.getNickname());

        // 4. 게시글 ID 설정 및 서비스 호출
        comment.setPosts_id(postId);
        commentService.add(comment);

        return "redirect:/post/detail/" + postId;
    }

    // 댓글 수정
    @PostMapping("/comment/edit")
    public String userEdit(@ModelAttribute Comment comment, RedirectAttributes redirectAttributes, @RequestParam int postId){
        comment.setPosts_id(postId);
        commentService.update(comment);
        redirectAttributes.addAttribute("id", comment.getId());
        return "redirect:/post/detail/"+postId;
    }




}