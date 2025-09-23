package com.community.course.service;

import com.community.course.entity.Course;
import com.community.course.repository.CourseRepo;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.containers.mp4.demuxer.MP4Demuxer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    private final String uploadPath = "C:/kkh/webDev/workspace/TestPJ/build/resources/main/static/uploads";

    public void uploadCourse(String coursesName, String description,
                             MultipartFile videoFile, MultipartFile thumbnailFile, Course.CourseCategory courses_category) throws IOException {

        // 1. 파일 저장
        String videoPath = saveFile(videoFile, "videos");
        String thumbnailPath = saveFile(thumbnailFile, "thumbnails");

        // 2. Course 엔티티 생성 및 데이터베이스 저장
        Course course = new Course();
        course.setCourses_name(coursesName);
        course.setDescription(description);
        course.setVideo_url(videoPath);
        course.setThumbnail_url(thumbnailPath);
        course.setCourses_category(courses_category);

        courseRepo.save(course);
    }

    public String saveFile(MultipartFile file, String subDir) throws IOException {
        Path directory = Paths.get(uploadPath, subDir);
        File destDir = directory.toFile();
        if (!destDir.exists()) {
            destDir.mkdirs(); // 디렉터리가 없으면 생성
        }

        String storedFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        File destFile = new File(destDir, storedFileName);

        file.transferTo(destFile); // 파일 저장

        return "/uploads/" + subDir + "/" + storedFileName;
    }


}

