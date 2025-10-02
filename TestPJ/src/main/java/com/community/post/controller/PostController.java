package com.bproject.post.controller;

import com.bproject.post.PostDTO;
import com.bproject.post.comment.Comment;
import com.bproject.post.comment.CommentService;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final PostRepository postRepository;

    // 메인에 게시글 리스트 출력
    @GetMapping("/post/list")
    public String postList(
            Model model,
            @PageableDefault(size = 10, sort = "created_at", direction = Sort.Direction.DESC) Pageable pageable) {

        // 1. 고정된 게시글 목록을 가져옵니다. (페이지네이션 미적용)
        List<Post> fixedPosts = postRepository.findByFixedTrue();
        model.addAttribute("fixedPosts", fixedPosts);

        // 2. 일반 게시글 목록을 가져옵니다. (페이지네이션 적용)
        Page<Post> normalPostsPage = postRepository.findByFixedFalse(pageable);
        model.addAttribute("normalPostsPage", normalPostsPage);

        return "post/main";
    }

    //검색
    @GetMapping("/post/search")
    public String search(@ModelAttribute PostDTO dto,
                         @PageableDefault(size = 10, sort = "created_at", direction = Sort.Direction.DESC) Pageable pageable,
                         Model model) {
        Page<Post> postPage = postService.searchPosts(dto, pageable);
        model.addAttribute("postPage", postPage);
        model.addAttribute("postList", postPage.getContent()); // 템플릿의 `postList`를 위해 추가
        model.addAttribute("searchType", dto.getSearchType()); // 검색 조건 유지를 위해 추가
        model.addAttribute("keyword", dto.getKeyword()); // 검색 조건 유지를 위해 추가
        return "post/searchResult";
    }

    // 게시글 추가(페이지 연결)
    @GetMapping("/post/add")
    public String addPostForm(Model model, HttpSession session) {

        // 1. 세션에서 로그인 유저 정보 가져오기
        User loginUser = (User) session.getAttribute("loginUser");

        // 2. 로그인 유저의 Role 정보를 모델에 담기
        String userRole = "GUEST"; // 기본값 (로그인 안 한 경우)
        if (loginUser != null) {
            // User 엔티티에서 role 필드를 가져와 String으로 변환 (예: "ADMIN", "USER", "INSTRUCTOR")
            userRole = loginUser.getRole().name();
        }

        // 모델에 role 정보와 Post 객체 전달
        model.addAttribute("post", new Post()); // Post 객체도 필요하므로 다시 추가
        model.addAttribute("userRole", userRole);

        return "post/addPost";
    }

    // 게시글 추가(버튼 클릭하여 실제로 게시글 추가)
    @PostMapping("/post/add")
    public String addPost(@ModelAttribute Post post, HttpSession session) { // HttpSession 추가
        // 1. 세션에서 로그인 유저 정보 가져오기
        User loginUser = (User) session.getAttribute("loginUser");

        // 2. 로그인 유저가 없을 경우 처리 (예: 로그인 페이지로 리다이렉트 또는 에러 처리)
        if (loginUser == null) {
            // 실제 운영 환경에서는 적절한 예외 처리나 로그인 페이지 리다이렉션이 필요합니다.
            // 여기서는 임시로 에러를 출력하고 리스트로 리다이렉트합니다.
            System.out.println("ERROR: 게시글 작성 시 로그인된 유저 정보가 없습니다.");
            return "redirect:/login"; // 예시: 로그인 페이지로 리다이렉트
        }

        // 3. Post 엔티티의 author 필드에 로그인 유저의 nickname 설정 (핵심 로직)
        post.setAuthor(loginUser.getNickname());

        // 4. 서비스 호출
        postService.add(post);

        return "redirect:/post/list";
    }

    // 게시글 보기 (제목(title) 클릭해서 연결되는 페이지) -> 9.9 <br> 태그 문제 해결
    @GetMapping("/post/detail/{id}")
    public String postDetailForm(@PathVariable int id, Model model, HttpSession session){ // ⭐️ HttpSession 추가

        // 1. 게시글 데이터 가져오기
        Post post = (Post) postService.findById(id).orElseThrow(
                ()->new IllegalArgumentException("Invalid post id: " + id));

        String content = Optional.ofNullable(post.getContent()).orElse("");
        String contentWithBr = content.replace("\n", "<br>");
        model.addAttribute("post", post);
        model.addAttribute("contentWithBr", contentWithBr);
        System.out.println("post : " + post);

        // 2. 로그인 유저 정보 추출 및 모델에 담기 (추가된 핵심 로직)
        User loginUser = (User) session.getAttribute("loginUser");

        String loginUserNickname = null;
        String userRole = "GUEST"; // 기본값

        if (loginUser != null) {
            loginUserNickname = loginUser.getNickname();
            userRole = loginUser.getRole().name(); // 예: "ADMIN", "USER"
        }

        // HTML 조건부 렌더링을 위해 모델에 추가
        model.addAttribute("loginUserNickname", loginUserNickname);
        model.addAttribute("userRole", userRole);

        // 3. 다음/이전 게시글 ID 찾기
        Optional<Integer> nextId = postService.findNextPostId(id);
        Optional<Integer> prevId = postService.findPrevPostId(id);

        System.out.println("Next ID: " + nextId);
        System.out.println("Prev ID: " + prevId);

        // 4. Optional 객체에서 값 가져와서 모델에 추가
        nextId.ifPresent(next -> model.addAttribute("nextId", next));
        prevId.ifPresent(prev -> model.addAttribute("prevId", prev));

        // 5. 댓글 처리
        List<Comment> comments = commentService.findById(id);

        List<Comment> processedComments = comments.stream()
                .map(comment -> {
                    String commentContent = Optional.ofNullable(comment.getComments_content()).orElse("");
                    String processedContent = commentContent.replace("\n", "<br>");
                    comment.setComments_content(processedContent);
                    return comment;
                })
                .collect(Collectors.toList());
        model.addAttribute("comments", processedComments);

        // 6. 댓글 등록 폼을 위한 객체
        model.addAttribute("comment", new Comment());

        return "post/detailPost";
    }

    // 게시글 삭제
    @PostMapping("/post/delete")
    public String deletePost(@RequestParam int id) {
        postService.deleteById(id);
        return "redirect:/post/list";
    }

    //게시글 수정(수정 페이지(editPost)로 연결)
    @GetMapping("/post/edit/{id}")
    public String postEditForm(@PathVariable int id, Model model){
        Post post = (Post) postService.findById(id).orElseThrow(
                ()->new IllegalArgumentException("Invaild post id "));
        model.addAttribute("post",post);
        System.out.println("post : "+post);
        return "post/editPost";
    }

    //게시글 수정(버튼 클릭하여 실제로 수정)
    @PostMapping("/post/edit")
    public String userEdit(@ModelAttribute Post post, RedirectAttributes redirectAttributes){
        postService.update(post);
        redirectAttributes.addAttribute("id", post.getId());
        return "redirect:/post/detail/{id}";
    }




}