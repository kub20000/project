package com.bproject.userscourses;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UCRepo {

    private final JdbcTemplate jdbc;

    private RowMapper<UsersCourses> usersCoursesRowMapper() {
        return (rs, rowNum) -> new UsersCourses(
                rs.getInt("id"),
                rs.getInt("courses_id"),
                rs.getInt("users_id"),
                rs.getInt("progress"),
                rs.getInt("duration_sec")
        );
    }

    // 1. 특정 유저의 특정 강의 진도율 기록을 조회 (duration_sec 포함)
    public Optional<UsersCourses> findByUsersIdAndCoursesId(int usersId, int coursesId) {
        String sql = "SELECT id, courses_id, users_id, progress, duration_sec FROM userscourses WHERE users_id = ? AND courses_id = ?";
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, usersCoursesRowMapper(), usersId, coursesId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // 2. 진도율 기록을 저장 (duration_sec 포함)
    public void save(UsersCourses usersCourses) {
        String sql = """
            INSERT INTO userscourses (courses_id, users_id, progress, duration_sec) 
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE 
                progress = VALUES(progress), 
                duration_sec = VALUES(duration_sec)
            """;
        jdbc.update(sql,
                usersCourses.getCourses_id(),
                usersCourses.getUsers_id(),
                usersCourses.getProgress(),
                usersCourses.getDuration_sec()
        );
    }

    public long getWatchedTimeByCategory(int userId, String category) {
        String sql = """
            SELECT COALESCE(SUM(uc.duration_sec), 0)
            FROM userscourses uc
            JOIN courses c ON uc.courses_id = c.id
            WHERE uc.users_id = ? AND c.courses_category = ?
            """;
        return jdbc.queryForObject(sql, Long.class, userId, category);
    }

}
