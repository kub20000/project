package com.bproject.videoHistory;

import com.bproject.course.repository.CourseRepo;
import com.bproject.mypage.ProgressDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VHService {

    private final VHRepo vhRepo;
    private final CourseRepo courseRepo;

    public Page<VideoHistoryDto> findRecentWatchedVideos(int userId, Pageable pageable) {

        Page<VideoHistory> historyPage = vhRepo.findRecentWatchedVideos(userId, pageable);

        // Entity를 DTO로 변환
        return historyPage.map(this::convertToDto);
    }

    private VideoHistoryDto convertToDto(VideoHistory history) {

        // 1. CourseRepository를 사용하여 실제 강의 제목과 썸네일 URL을 조회합니다.
        CourseRepo.CourseDetails details = courseRepo.findCourseDetailsById(history.getVideo_id());

        return new VideoHistoryDto(
                history.getVideo_id(),
                details.getCourses_name(),
                details.getThumbnail_url(),
                history.getWatched_date(),
                history.getProgress_rate()
        );
    }

    @Transactional
    public void saveNewHistory(int userId, int videoId, int progressRate) {

        // 💡 비즈니스 로직: 진도율이 100%를 초과할 수 없도록 보정
        int finalProgress = Math.min(100, Math.max(0, progressRate));

        // Repository를 호출하여 DB에 기록을 남깁니다.
        vhRepo.saveOrUpdateHistory(userId, videoId, finalProgress);
    }


}