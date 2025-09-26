package com.community.course;

import com.community.course.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoursePageDto {
    private List<Course> content;
    private int currentPage;
    private int totalPages;
    private long totalItems;
}
