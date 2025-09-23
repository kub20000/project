package com.community.post.controller;

import com.community.post.PostDTO;
import com.community.post.comment.Comment;
import com.community.post.comment.CommentService;
import com.community.post.entity.Post;
import com.community.post.repository.PostRepository;
import com.community.post.service.PostService;
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
    public String addForm(Model model) {
        model.addAttribute("post", new Post());
        return "post/addPost";
    }

    // 게시글 추가(버튼 클릭하여 실제로 게시글 추가)
    @PostMapping("/post/add")
    public String addPost(@ModelAttribute Post post) {
        postService.add(post);
        return "redirect:/post/list";
    }

    // 게시글 보기 (제목(title) 클릭해서 연결되는 페이지) -> 9.9 <br> 태그 문제 해결
    @GetMapping("/post/detail/{id}")
    public String postDetailForm(@PathVariable int id, Model model){

        //1. 게시글 데이터 가져오기
        Post post = (Post) postService.findById(id).orElseThrow(
                ()->new IllegalArgumentException("Invaild id"));
        String content = Optional.ofNullable(post.getContent()).orElse("");
        String contentWithBr = content.replace("\n", "<br>");
        model.addAttribute("post",post);
        model.addAttribute("contentWithBr",contentWithBr);
        System.out.println("post : "+post);

        // 2. 다음/이전 게시글 ID 찾기
        Optional<Integer> nextId = postService.findNextPostId(id);
        Optional<Integer> prevId = postService.findPrevPostId(id);

        System.out.println("Next ID: " + nextId);
        System.out.println("Prev ID: " + prevId);

        // 3. Optional 객체에서 값 가져와서 모델에 추가
        nextId.ifPresent(next -> model.addAttribute("nextId", next));
        prevId.ifPresent(prev -> model.addAttribute("prevId", prev));

        //4. 댓글
        List<Comment> comments = commentService.findById(id);
        // 한줄 내려쓰기 판단
        List<Comment> processedComments = comments.stream()
                .map(comment -> {
                    String commentContent = comment.getComments_content();
                    String processedContent = commentContent.replace("\n", "<br>");
                    comment.setComments_content(processedContent);
                    return comment;
                })
                .collect(Collectors.toList());
        model.addAttribute("comments", processedComments);

        // 댓글
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
