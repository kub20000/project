package com.bproject.course;

import com.bproject.course.entity.Course;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class CourseDetailDto {

    // Course 엔티티의 기본 필드
    private int id;
    private String courses_name;
    private String description;
    private String video_url;
    private String thumbnail_url;
    private Course.CourseCategory courses_category;
    private int total_sec;
    private int like_count;
    private int instructor_id; // ⭐️ [추가] Course 엔티티 변경 반영

    // 유저별 진도 데이터 (프론트엔드가 요청하는 필드명)
    private int userDurationSec;

    // Course 엔티티를 기반으로 DTO를 생성하는 생성자
    public CourseDetailDto(Course course, int userDurationSec) {
        this.id = course.getId();
        this.courses_name = course.getCourses_name();
        this.description = course.getDescription();
        this.video_url = course.getVideo_url();
        this.thumbnail_url = course.getThumbnail_url();
        this.courses_category = course.getCourses_category();
        this.total_sec = course.getTotal_sec();
        this.like_count = course.getLike_count();
        this.instructor_id = course.getInstructor_id(); // ⭐️ [추가] instructor_id 매핑

        // 유저별 데이터를 주입
        this.userDurationSec = userDurationSec;
    }
}