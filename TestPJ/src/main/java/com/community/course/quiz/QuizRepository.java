package com.community.course.quiz;

import io.micrometer.common.lang.NonNullApi;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@NonNullApi
public interface QuizRepository extends CrudRepository<Quiz, Long> {

    Optional<Quiz> findById(Long id);

    // 강의 ID에 해당하는 모든 퀴즈를 조회
    @Query("SELECT q FROM Quiz q WHERE q.courses_id = :courses_id ORDER BY q.id ASC")
    List<Quiz> findByCourses_id(@Param("courses_id") Long courses_id);

    // 현재 퀴즈 ID보다 작은 ID를 가진 퀴즈 중 가장 큰 ID를 가진 퀴즈
    @Query("SELECT q FROM Quiz q WHERE q.id < :id AND q.courses_id = :courses_id ORDER BY q.id DESC LIMIT 1")
    Optional<Quiz> findFirstByIdLessThanAndCourses_idOrderByIdDesc(@Param("id") Long id, @Param("courses_id") Long courses_id);

    // 현재 퀴즈 ID보다 큰 ID를 가진 퀴즈 중 가장 작은 ID를 가진 퀴즈
    @Query("SELECT q FROM Quiz q WHERE q.id > :id AND q.courses_id = :courses_id ORDER BY q.id ASC LIMIT 1")
    Optional<Quiz> findFirstByIdGreaterThanAndCourses_idOrderByIdAsc(@Param("id") Long id, @Param("courses_id") Long courses_id);

    // 강의 ID를 기준으로 가장 첫 번째 퀴즈
    @Query("SELECT q FROM Quiz q WHERE q.courses_id = :courses_id ORDER BY q.id ASC LIMIT 1")
    Optional<Quiz> findFirstByCourses_idOrderByIdAsc(@Param("courses_id") Long courses_id);
}
