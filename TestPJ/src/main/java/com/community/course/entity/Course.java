package com.community.course.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private int id;
    private int course_id;
    private String title;
    private String video_url;
    private int duration_sec;
    private int sort_order;
}
