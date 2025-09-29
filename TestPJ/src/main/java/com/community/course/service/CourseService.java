package com.community.course.service;

import com.community.course.CoursePageDto;
import com.community.course.entity.Course;
import com.community.course.repository.CourseRepo;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.demuxer.MP4Demuxer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    // @Value 어노테이션은 필드에 직접 주입할 수 있도록 변경
    @Value("${file.upload-path}")
    private String uploadPath;

    public CourseService(CourseRepo courseRepo) {
        this.courseRepo = courseRepo;
    }

    public Optional<Course> findById(int courseId) {
        return courseRepo.findById(courseId);
    }

    public List<Course> findAll() {
        return courseRepo.findAll();
    }

    public int increaseLikeCount(int courseId) {
        return courseRepo.increaseLikeCount(courseId);
    }

    public int decreaseLikeCount(int courseId) {
        return courseRepo.decreaseLikeCount(courseId);
    }

    public Course uploadCourse(String coursesName, String description,
                               MultipartFile videoFile, MultipartFile thumbnailFile,
                               Course.CourseCategory courses_category) throws IOException {

        File videoTempFile = saveFileAndGetFile(videoFile, "videos");
        String videoPath = "/uploads/videos/" + videoTempFile.getName();
        String thumbnailPath = saveFile(thumbnailFile, "thumbnails");

        int totalSeconds = getVideoDurationInSeconds(videoTempFile);

        Course course = new Course();
        course.setCourses_name(coursesName);
        course.setDescription(description);
        course.setVideo_url(videoPath);
        course.setThumbnail_url(thumbnailPath);
        course.setCourses_category(courses_category);
        course.setTotal_sec(totalSeconds);

        courseRepo.save(course);

        return course;
    }

    public File saveFileAndGetFile(MultipartFile file, String subDir) throws IOException {
        Path directory = Paths.get(uploadPath, subDir);
        File destDir = directory.toFile();
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        String storedFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        File destFile = new File(destDir, storedFileName);

        file.transferTo(destFile);

        return destFile;
    }

    public String saveFile(MultipartFile file, String subDir) throws IOException {
        Path directory = Paths.get(uploadPath, subDir);
        File destDir = directory.toFile();
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        String storedFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        File destFile = new File(destDir, storedFileName);

        file.transferTo(destFile);

        return "/uploads/" + subDir + "/" + storedFileName;
    }

    public int getVideoDurationInSeconds(File videoFile) {
        FileChannelWrapper ch = null;
        try {
            ch = NIOUtils.readableFileChannel(videoFile.getAbsolutePath());
            MP4Demuxer demuxer = MP4Demuxer.createMP4Demuxer(ch);

            MovieBox moov = demuxer.getMovie();

            if (moov != null) {
                long duration = moov.getDuration();
                long timeScale = moov.getTimescale();

                if (timeScale > 0) {
                    return (int) (duration / timeScale);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            NIOUtils.closeQuietly(ch);
        }
        return 0;
    }

    //강의 수정
    public void updateCourse(int id, String coursesName, String description,
                             MultipartFile videoFile, MultipartFile thumbnailFile, Course.CourseCategory courses_category,
                             String existingVideoUrl, String existingThumbnailUrl) throws IOException {

        Course existingCourse = courseRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course id: " + id));

        // 기존 파일을 삭제하는 로직을 업데이트된 경로에 맞게 수정
        if (videoFile != null && !videoFile.isEmpty()) {
            deleteExistingFile(existingCourse.getVideo_url());
        }
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            deleteExistingFile(existingCourse.getThumbnail_url());
        }


        // 파일 업로드 처리
        String videoUrl = existingVideoUrl;
        int totalSeconds = existingCourse.getTotal_sec();
        if (videoFile != null && !videoFile.isEmpty()) {
            File tempFile = saveFileAndGetFile(videoFile, "videos");
            videoUrl = "/uploads/videos/" + tempFile.getName();
            totalSeconds = getVideoDurationInSeconds(tempFile);
        }

        String thumbnailUrl = existingThumbnailUrl;
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            thumbnailUrl = saveFile(thumbnailFile, "thumbnails");
        }

        // Course 객체 업데이트
        existingCourse.setCourses_name(coursesName);
        existingCourse.setDescription(description);
        existingCourse.setCourses_category(courses_category);
        existingCourse.setVideo_url(videoUrl);
        existingCourse.setThumbnail_url(thumbnailUrl);
        existingCourse.setTotal_sec(totalSeconds);

        courseRepo.update(existingCourse);
    }

    private void deleteExistingFile(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            // application.properties에 설정된 uploadPath와 동일한 기준 경로 사용
            String fullPath = uploadPath + filePath.replace("/uploads", "");

            // 로그를 추가하여 실제 삭제를 시도하는 경로를 확인
            System.out.println("삭제를 시도하는 파일 경로: " + fullPath);

            try {
                File file = new File(fullPath);
                if (file.exists()) {
                    Files.delete(Paths.get(fullPath));
                    System.out.println("기존 파일 삭제 성공: " + fullPath);
                } else {
                    System.out.println("삭제할 파일이 존재하지 않습니다: " + fullPath);
                }
            } catch (IOException e) {
                System.err.println("기존 파일 삭제 실패: " + fullPath);
                e.printStackTrace();
                // 파일 삭제 실패 시, 예외를 던져 트랜잭션 롤백을 유도할 수 있습니다.
                throw new RuntimeException("파일 삭제 실패", e);
            }
        }
    }

    // 강의 삭제
    @Transactional
    public void deleteCourse(int courseId) {
        Course courseToDelete = courseRepo.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course ID: " + courseId));

        // 파일 삭제
        deleteExistingFile(courseToDelete.getVideo_url());
        deleteExistingFile(courseToDelete.getThumbnail_url());

        // DB 레코드 삭제
        courseRepo.deleteById(courseId);
    }

    // 내 강의 페이지네이션
    public CoursePageDto findCoursesWithFilterAndPagination(int page, int size, String search, String category) {
        int offset = page * size;
        List<Course> courses = courseRepo.findWithFilterAndPagination(size, offset, search, category);
        long totalItems = courseRepo.countWithFilterAndSearch(search, category);
        int totalPages = (int) Math.ceil((double) totalItems / size);

        return new CoursePageDto(courses, page, totalPages, totalItems);
    }

    @Transactional
    public void updateProgress(int courseId, int durationSec) {
        courseRepo.updateProgress(courseId, durationSec);
    }

}