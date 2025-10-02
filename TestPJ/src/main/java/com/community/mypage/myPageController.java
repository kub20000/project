package com.bproject.mypage;

import com.bproject.user.entity.User;
import com.bproject.videoHistory.VHService;
import com.bproject.videoHistory.VideoHistoryDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class myPageController {

    private final myPageService myPageService;
    private final VHService vhService;

    // 마이페이지 대시보드 (메인) 요청 처리
    @GetMapping("/dashBoard")
    public String dashboard(HttpSession session, Model model) {

        // 세션에서 로그인 사용자 정보를 가져옵니다.
        User loginUser = (User) session.getAttribute("loginUser");

        // 1. 로그인 체크: 세션이 없으면 로그인 페이지로 리다이렉트합니다.
        if (loginUser == null) {
            // 이 로그가 찍히면 로그인이 안된 상태로 접근했다는 뜻입니다.
            System.out.println("[View Controller] 로그인 데이터 없음. /login으로 리다이렉트.");
            return "redirect:/login";
        }

        // 2. Model에 loginUser 객체를 명시적으로 추가하여 Thymeleaf에서 사용 가능하게 합니다.
        model.addAttribute("loginUser", loginUser);

        // 3. 진도율 데이터 가져오기
        // (세션 체크 후 실행되므로 loginUser.getId()는 안전합니다.)
        int userId = loginUser.getId();
        model.addAttribute("progressList", myPageService.getProgressRateByCategories(userId));

        // 4. Thymeleaf 템플릿 경로를 반환합니다.
        return "mypage/dashBoard";
    }

    // 내 학습기록
    @GetMapping("/myStudy")
    public String myStudy(HttpSession session,
                           Model model,
                           @PageableDefault(size = 10, sort = "watchedDate", direction = Sort.Direction.DESC) Pageable pageable) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            System.out.println("[MyStudy] 로그인 데이터 없음. /login으로 리다이렉트.");
            return "redirect:/login";
        }

        int userId = loginUser.getId();

        // 1. 카테고리별 진도율 데이터 바인딩 (대시보드와 동일)
        model.addAttribute("progressList", myPageService.getProgressRateByCategories(userId));

        // 2. 최근 본 영상 목록 (페이지네이션 적용)
        Page<VideoHistoryDto> historyPage = vhService.findRecentWatchedVideos(userId, pageable);
        model.addAttribute("historyPage", historyPage);

        return "mypage/myStudy";
    }




}