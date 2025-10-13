package com.bproject.course.service;

import com.bproject.GcsService;
import com.bproject.course.CourseDetailDto;
import com.bproject.course.CoursePageDto;
import com.bproject.course.entity.Course;
import com.bproject.course.repository.CourseRepo;
import com.bproject.userscourses.UCService;
import com.bproject.userscourses.UsersCourses;
import lombok.RequiredArgsConstructor;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepo courseRepo;
    private final UCService ucService;
    private final GcsService gcsService;

    public Optional<Course> findById(int courseId) {
        return courseRepo.findById(courseId);
    }

    public int increaseLikeCount(int courseId) {
        return courseRepo.increaseLikeCount(courseId);
    }

    public int decreaseLikeCount(int courseId) {
        return courseRepo.decreaseLikeCount(courseId);
    }

    public Course uploadCourse(String coursesName, String description,
                               MultipartFile videoFile, MultipartFile thumbnailFile,
                               Course.CourseCategory courses_category,
                               int instructorId) throws IOException {

        File tempVideoFile = null;
        int totalSeconds = 0;

        try {
            //동영상 길이 측정을 위해 MultipartFile을 로컬 파일로 임시 저장
            tempVideoFile = convertMultipartFileToFile(videoFile);
            totalSeconds = getVideoDurationInSeconds(tempVideoFile); // 길이 측정

            // GCS에 영상 업로드 및 URL 획득 (측정 후 GCS로 업로드)
            String videoUrl = gcsService.uploadFile(videoFile, "videos");
            String thumbnailUrl = gcsService.uploadFile(thumbnailFile, "thumbnails");

            // Course 객체 생성 및 DB 저장
            Course course = new Course();
            course.setCourses_name(coursesName);
            course.setDescription(description);
            course.setVideo_url(videoUrl);
            course.setThumbnail_url(thumbnailUrl);
            course.setCourses_category(courses_category);
            course.setTotal_sec(totalSeconds); //  측정된 길이 저장
            course.setInstructor_id(instructorId);

            courseRepo.save(course);
            return course;

        } finally {
            // [핵심] 임시 파일 삭제
            if (tempVideoFile != null) {
                tempVideoFile.delete();
            }
        }
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        // 임시 파일 경로: 시스템의 기본 임시 디렉토리 사용
        Path tempPath = Paths.get(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString() + "_" + file.getOriginalFilename());

        // MultipartFile의 내용을 임시 파일로 복사
        Files.copy(file.getInputStream(), tempPath);
        return tempPath.toFile();
    }

    public int getVideoDurationInSeconds(File videoFile) {
        FileChannelWrapper ch = null;
        try {
            //  File 객체를 사용하여 채널 열기
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

        File tempVideoFile = null; //  임시 파일 변수 선언
        int totalSeconds = existingCourse.getTotal_sec(); // 기존 길이를 기본값으로 사용

        try {
            // 기존 GCS 파일 삭제 및 파일 업로드 처리
            String videoUrl = existingVideoUrl;
            if (videoFile != null && !videoFile.isEmpty()) {
                // 기존 파일 삭제
                deleteExistingFile(existingCourse.getVideo_url());

                //  길이 측정 및 totalSeconds 업데이트
                tempVideoFile = convertMultipartFileToFile(videoFile); // 임시 파일 생성
                totalSeconds = getVideoDurationInSeconds(tempVideoFile); // 길이 측정

                // GCS에 새 파일 업로드
                videoUrl = gcsService.uploadFile(videoFile, "videos");
            }

            String thumbnailUrl = existingThumbnailUrl;
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                // 기존 썸네일 삭제
                deleteExistingFile(existingCourse.getThumbnail_url());
                // GCS에 새 파일 업로드
                thumbnailUrl = gcsService.uploadFile(thumbnailFile, "thumbnails");
            }

            // Course 객체 업데이트
            existingCourse.setCourses_name(coursesName);
            existingCourse.setDescription(description);
            existingCourse.setCourses_category(courses_category);
            existingCourse.setVideo_url(videoUrl);
            existingCourse.setThumbnail_url(thumbnailUrl);
            existingCourse.setTotal_sec(totalSeconds); //  업데이트된 길이 저장

            courseRepo.update(existingCourse);

        } finally {
            // 임시 파일 정리
            if (tempVideoFile != null) {
                tempVideoFile.delete();
            }
        }
    }

    private void deleteExistingFile(String filePath) {
        gcsService.deleteFile(filePath);
    }

    // 강의 삭제
    @Transactional
    public void deleteCourse(int courseId) {
        Course courseToDelete = courseRepo.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course ID: " + courseId));

        // GCS 파일 삭제
        deleteExistingFile(courseToDelete.getVideo_url());
        deleteExistingFile(courseToDelete.getThumbnail_url());

        // DB 레코드 삭제
        courseRepo.deleteById(courseId);
    }

    // myCourse id데이터 전달
    public CoursePageDto findCoursesByInstructor(int instructorId, int page, int size, String search, String category) {
        int offset = page * size;

        // 레포지토리에 instructorId를 전달하여 해당 강사의 강의만 조회하도록 수정
        List<Course> courses = courseRepo.findInstructorCourses(
                instructorId, size, offset, search, category
        );

        // 전체 항목 개수도 instructorId를 기준으로 다시 계산
        long totalItems = courseRepo.countInstructorCourses(instructorId, search, category);
        int totalPages = (int) Math.ceil((double) totalItems / size);

        return new CoursePageDto(courses, page, totalPages, totalItems);
    }
    // 인기 강의 조회
    public List<Course> getTop3PopularCourses() {
        return courseRepo.findTop3PopularCourses();
    }

    // 진도율
    public int getUserDurationSecForClient(int userId, int courseId) {
        return ucService.getUserDurationSec(userId, courseId);
    }

    public List<CourseDetailDto> findAllCoursesWithProgress(int userId) {
        // 1. 모든 강의 목록 (Course 엔티티)을 가져옵니다.
        List<Course> courses = courseRepo.findAll();

        // 2. 각 Course를 CourseDetailDto로 변환하고 진도율 정보를 주입합니다.
        return courses.stream()
                .map(course -> {
                    CourseDetailDto dto = new CourseDetailDto(course, 0); // 기본 DTO 생성 (시청 시간 0으로 시작)

                    if (userId > 0) { // 로그인 유저일 경우에만 진도율을 조회
                        UsersCourses userProgress = ucService.getUsersCourses(userId, course.getId());

                        if (userProgress != null) {
                            dto.setUserDurationSec(userProgress.getDuration_sec());
                        }
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }


}