package com.bproject.userscourses;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UCService {

    private final UCRepo UCRepo;

    public UsersCourses getUsersCourses(int userId, int courseId) {
        return UCRepo.findByUsersIdAndCoursesId(userId, courseId).orElse(null);
    }

    public int getUserDurationSec(int userId, int courseId) {
        return UCRepo.findByUsersIdAndCoursesId(userId, courseId)
                .map(UsersCourses::getDuration_sec)
                .orElse(0);
    }

    @Transactional
    public void saveOrUpdateProgress(int usersId, int coursesId, int newProgress, int durationSec) {
        // 🚨 UCRepo에 updateProgressAndDuration 메서드가 더 이상 필요 없습니다.
        // UCRepo의 save() 메서드가 INSERT ... ON DUPLICATE KEY UPDATE를 수행하도록 구현했기 때문입니다.

        UsersCourses record = new UsersCourses();
        record.setUsers_id(usersId);
        record.setCourses_id(coursesId);
        record.setProgress(newProgress);
        record.setDuration_sec(durationSec);

        // save()만 호출하면, 기존 데이터가 있을 경우 업데이트되고, 없으면 새로 삽입됩니다.
        UCRepo.save(record);
    }






}
