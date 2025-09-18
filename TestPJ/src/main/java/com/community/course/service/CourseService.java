package com.community.course.service;

import com.community.course.entity.Course;
import com.community.course.repository.CourseRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepo courseRepo;

    public CourseService(CourseRepo courseRepo) {
        this.courseRepo = courseRepo;
    }

    public Optional<Course> findById(int courseId) {
        // .get()을 사용하여 Optional 객체에서 Course 객체를 직접 가져옴
        return courseRepo.findById(courseId);
    }

    public List<Course> findAll() {
        return courseRepo.findAll(); // 모든 강의 목록을 가져오는 메서드
    }

    // 좋아요 수 증가 및 업데이트된 값 반환
    public int increaseLikeCount(int courseId) {
        return courseRepo.increaseLikeCount(courseId);
    }

    // 좋아요 수 감소 및 업데이트된 값 반환
    public int decreaseLikeCount(int courseId) {
        return courseRepo.decreaseLikeCount(courseId);
    }

}
