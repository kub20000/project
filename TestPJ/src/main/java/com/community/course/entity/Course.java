package com.community.course.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    public enum CourseCategory {
        LIFE, SKILL, RECIPE
    }

    private int id;
    private String courses_name;
    private CourseCategory courses_category;
    private String description;
    private String video_url;
    private int duration_sec;
    private int like_count;
    private int total_sec;
    private String thumbnail_url;
}