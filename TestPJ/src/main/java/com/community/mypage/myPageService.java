package com.bproject.mypage;

import com.bproject.course.repository.CourseRepo;
import com.bproject.post.PostPageDto;
import com.bproject.post.entity.Post;
import com.bproject.post.repository.PostRepo;
import com.bproject.userscourses.UCRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class myPageService {

    private final PostRepo postRepo;
    private final CourseRepo courseRepo;
    private final UCRepo ucRepo;

    // 내 게시글 목록 조회 및 페이지네이션
    public PostPageDto findMyPosts(String authorNickname, int page, int size, String keyword) {
        int offset = page * size;

        // 1. 로그인 유저의 게시글만 조회
        List<Post> posts = postRepo.findMyPostsWithPagination(authorNickname, size, offset, keyword);

        // 2. 전체 개수 카운트
        long totalItems = postRepo.countMyPosts(authorNickname, keyword);
        int totalPages = (int) Math.ceil((double) totalItems / size);

        // 3. PostPageDto 객체 생성 및 반환
        return new PostPageDto(posts, page, totalPages, totalItems);
    }

    public boolean deletePost(int postId, String authorNickname) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 1. 닉네임 일치 확인 (권한 체크)
        if (!post.getAuthor().equals(authorNickname)) {
            // 권한 없음
            return false;
        }

        // 2. 권한이 있다면 삭제
        postRepo.deleteById(postId);
        return true;
    }

    public List<ProgressDto> getProgressRateByCategories(int userId) {
        // Course 엔티티의 Category enum에 맞는 문자열 사용
        List<String> categories = Arrays.asList("LIFE", "SKILL", "RECIPE");

        return categories.stream().map(category -> {
            // 1. 카테고리 전체 강의 총 길이 (초) - CourseRepo 사용
            long totalDuration = courseRepo.getTotalDurationByCategory(category);

            // 2. 유저가 시청한 총 시간 (초) - UCRepo 사용
            long watchedTime = ucRepo.getWatchedTimeByCategory(userId, category);

            // 3. 진도율 계산
            double progressRate = 0.0;
            if (totalDuration > 0) {
                // 진도율은 100%를 넘을 수 없도록 Math.min 함수 적용
                progressRate = Math.min(1.0, (double) watchedTime / totalDuration) * 100;
            }

            return new ProgressDto(category, (int) Math.round(progressRate));

        }).collect(Collectors.toList());
    }







}
