package com.community.post.controller;

import com.community.post.entity.Post;
import com.community.post.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 메인에 게시글 리스트 출력
    @GetMapping("/post/list")
    public String postList(Model model) {
        System.out.println("postList");
        model.addAttribute("posts", postService.findAll());
        return "/post/main";
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
    public String userEdit(@ModelAttribute Post post){
        postService.update(post);
        return "redirect:/post/list";
    }


}
