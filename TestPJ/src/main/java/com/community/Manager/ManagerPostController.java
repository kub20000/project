package com.bproject.Manager;

import com.bproject.post.PostDTO;
import com.bproject.post.entity.Post;
import com.bproject.post.repository.PostRepository;
import com.bproject.post.service.PostService;
import com.bproject.user.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/manager")
public class ManagerPostController {

    private final PostService postService;
    private final PostRepository postRepository;

    // ======================================================================
    //                              [Manager] 목록 및 검색
    // ======================================================================

    /**
     * [Manager] 게시판 관리 페이지 (게시글 목록)
     * URL: /manager/postM
     * HTML: templates/manager/postM.html
     */
    @GetMapping("/postM")
    public String managerPostList(
            Model model,
            @PageableDefault(size = 10, sort = "created_at", direction = Sort.Direction.DESC) Pageable pageable) {

        // 모든 게시글을 페이지네이션 처리
        Page<Post> allPostsPage = postRepository.findAll(pageable);

        // HTML에서 사용한 변수명(normalPostsPage)을 유지하여 전달
        model.addAttribute("normalPostsPage", allPostsPage);

        return "manager/postM"; // 템플릿 파일 경로도 manager/postM.html로 변경 가정
    }

    /**
     * [Manager] 게시글 검색 기능
     * URL: /manager/post/search
     */
    @GetMapping("/post/search")
    public String managerPostSearch(@ModelAttribute PostDTO dto,
                                    @PageableDefault(size = 10, sort = "created_at", direction = Sort.Direction.DESC) Pageable pageable,
                                    Model model) {

        Page<Post> postPage = postService.searchPosts(dto, pageable);

        // 템플릿의 변수명(normalPostsPage)에 맞게 전달
        model.addAttribute("normalPostsPage", postPage);

        // 검색 조건 유지를 위해 모델에 추가
        model.addAttribute("searchType", dto.getSearchType());
        model.addAttribute("keyword", dto.getKeyword());

        return "manager/postM"; // 검색 후 Manager 목록 페이지로 이동
    }

    // ======================================================================
    //                              [Manager] 추가 기능
    // ======================================================================

    /**
     * [Manager] 게시글 추가 폼 페이지 연결 (기존 로직 재활용)
     * URL: /manager/post/add
     */
    @GetMapping("/post/add")
    public String addPostForm(Model model, HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");
        String userRole = "GUEST";
        if (loginUser != null) {
            userRole = loginUser.getRole().name();
        }

        model.addAttribute("post", new Post());
        model.addAttribute("userRole", userRole);

        // 실제 템플릿 파일은 /post/addPost.html을 재활용합니다.
        return "post/addPost";
    }

    /**
     * [Manager] 게시글 추가 처리 (기존 로직 재활용, 리다이렉트 경로만 변경)
     * URL: /manager/post/add
     */
    @PostMapping("/post/add")
    public String addPost(@ModelAttribute Post post, HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            System.out.println("ERROR: 게시글 작성 시 로그인된 유저 정보가 없습니다.");
            return "redirect:/login";
        }

        post.setAuthor(loginUser.getNickname());
        postService.add(post);

        // 글쓰기 완료 후 Manager 목록 페이지로 리다이렉트
        return "redirect:/manager/postM";
    }


    // ======================================================================
    //                              [Manager] 수정/삭제 기능
    // ======================================================================

    /**
     * [Manager] 게시글 수정 폼 페이지 연결
     * URL: /manager/post-edit?id={id}
     */
    @GetMapping("/post-edit")
    public String postEditForm(@RequestParam int id, Model model){
        Post post = (Post) postService.findById(id).orElseThrow(
                ()->new IllegalArgumentException("Invalid post id "));
        model.addAttribute("post",post);
        return "post/editPost"; // 기존 수정 페이지 재활용
    }

    /**
     * [Manager] 게시글 수정 처리
     * URL: /manager/post-edit
     */
    @PostMapping("/post-edit")
    public String postEdit(@ModelAttribute Post post, RedirectAttributes redirectAttributes){
        postService.update(post);
        redirectAttributes.addAttribute("id", post.getId());
        return "redirect:/manager/postM"; // 수정 후 Manager 목록으로 리다이렉트
    }

    /**
     * [Manager] 게시글 삭제 처리
     * URL: /manager/post-delete?id={id}
     */
    @GetMapping("/post-delete")
    public String deletePost(@RequestParam int id) {
        postService.deleteById(id);
        return "redirect:/manager/postM"; // 삭제 후 Manager 목록으로 리다이렉트
    }
}