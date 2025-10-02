package com.bproject.course.repository;

import com.bproject.course.entity.Course;
import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
public class CourseRepo {

    private final JdbcTemplate jdbc;

    public CourseRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Data
    public class CourseDetails {
        private String courses_name;
        private String thumbnail_url;
    }

    public List<Course> findAll() {
        String sql = "SELECT * FROM courses";
        return jdbc.query(sql, courseRowMapper());
    }

    public Optional<Course> findById(int id) {
        String sql = "SELECT * FROM courses WHERE id = ?";
        try {
            Course course = jdbc.queryForObject(sql, courseRowMapper(), id);
            return Optional.ofNullable(course);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private RowMapper<Course> courseRowMapper() {
        return (rs, rowNum) -> {
            Course c = new Course();
            c.setId(rs.getInt("id"));
            c.setCourses_name(rs.getString("courses_name"));
            c.setCourses_category(Course.CourseCategory.valueOf(rs.getString("courses_category").toUpperCase()));
            c.setDescription(rs.getString("description"));
            c.setVideo_url(rs.getString("video_url"));
            c.setLike_count(rs.getInt("like_count"));
            c.setTotal_sec(rs.getInt("total_sec"));
            c.setThumbnail_url(rs.getString("thumbnail_url"));
            c.setInstructor_id(rs.getInt("instructor_id"));
            return c;
        };
    }

    // 좋아요 수 증가 후 업데이트된 좋아요 수 반환
    public int increaseLikeCount(int courseId) {
        String updateSql = "UPDATE courses SET like_count = like_count + 1 WHERE id = ?";
        jdbc.update(updateSql, courseId);

        String selectSql = "SELECT like_count FROM courses WHERE id = ?";
        return jdbc.queryForObject(selectSql, Integer.class, courseId);
    }

    // 좋아요 수 감소 후 업데이트된 좋아요 수 반환
    public int decreaseLikeCount(int courseId) {
        String updateSql = "UPDATE courses SET like_count = like_count - 1 WHERE id = ?";
        jdbc.update(updateSql, courseId);

        String selectSql = "SELECT like_count FROM courses WHERE id = ?";
        return jdbc.queryForObject(selectSql, Integer.class, courseId);
    }

    public void save(Course course) {
        String sql = "INSERT INTO courses (courses_name, description, video_url, thumbnail_url, courses_category, total_sec, instructor_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, course.getCourses_name());
                ps.setString(2, course.getDescription());
                ps.setString(3, course.getVideo_url());
                ps.setString(4, course.getThumbnail_url());
                ps.setString(5, String.valueOf(course.getCourses_category()));
                ps.setInt(6, course.getTotal_sec());
                ps.setInt(7, course.getInstructor_id());
                return ps;
            }
        }, keyHolder);

        // 생성된 ID를 Course 객체에 다시 설정
        course.setId(keyHolder.getKey().intValue());
    }



    // 강의 수정
    public void update(Course course) {
        String sql = "UPDATE courses SET courses_name = ?, courses_category = ?, description = ?, video_url = ?, total_sec = ?, thumbnail_url = ? WHERE id = ?";
        jdbc.update(sql,
                course.getCourses_name(),
                course.getCourses_category().name().toUpperCase(),
                course.getDescription(),
                course.getVideo_url(),
                course.getTotal_sec(),
                course.getThumbnail_url(),
                course.getId()
        );
    }

    // 강사 ID를 기준으로 필터링된 강의 목록 조회
    public List<Course> findInstructorCourses(int instructorId, int limit, int offset, String search, String category) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM courses WHERE instructor_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(instructorId); // ⭐️ 강사 ID를 첫 번째 조건으로 추가

        // 동적 WHERE 절 구성 (instructor_id 조건 뒤에 추가)
        if (search != null && !search.isEmpty() && !category.equals("전체")) {
            sqlBuilder.append(" AND courses_name LIKE ? AND courses_category = ?");
            params.add("%" + search + "%");
            params.add(category);
        } else if (search != null && !search.isEmpty()) {
            sqlBuilder.append(" AND courses_name LIKE ?");
            params.add("%" + search + "%");
        } else if (!category.equals("전체")) {
            sqlBuilder.append(" AND courses_category = ?");
            params.add(category);
        }

        sqlBuilder.append(" LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        return jdbc.query(sqlBuilder.toString(), courseRowMapper(), params.toArray());
    }

    // 강사 ID를 기준으로 필터링된 강의 개수 조회
    public long countInstructorCourses(int instructorId, String search, String category) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(*) FROM courses WHERE instructor_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(instructorId); // ⭐️ 강사 ID를 첫 번째 조건으로 추가

        // 동적 WHERE 절 구성 (instructor_id 조건 뒤에 추가)
        if (search != null && !search.isEmpty() && !category.equals("전체")) {
            sqlBuilder.append(" AND courses_name LIKE ? AND courses_category = ?");
            params.add("%" + search + "%");
            params.add(category);
        } else if (search != null && !search.isEmpty()) {
            sqlBuilder.append(" AND courses_name LIKE ?");
            params.add("%" + search + "%");
        } else if (!category.equals("전체")) {
            sqlBuilder.append(" AND courses_category = ?");
            params.add(category);
        }

        return jdbc.queryForObject(sqlBuilder.toString(), Long.class, params.toArray());
    }

    // 강의 삭제
    public void deleteById(int id) {
        String sql = "DELETE FROM courses WHERE id = ?";
        jdbc.update(sql, id);
    }

    // 인기 강의 조회
    public List<Course> findTop3PopularCourses() {
        String sql = "SELECT * FROM courses ORDER BY like_count DESC LIMIT 3";
        return jdbc.query(sql, courseRowMapper());
    }

    public long getTotalDurationByCategory(String category) {
        String sql = "SELECT COALESCE(SUM(total_sec), 0) FROM courses WHERE courses_category = ?";
        return jdbc.queryForObject(sql, Long.class, category);
    }

    public String findCourseNameById(int courseId) {
        String sql = "SELECT courses_name FROM course WHERE id = ?";
        try {
            // queryForObject를 사용하여 String 타입의 단일 값을 조회
            return jdbc.queryForObject(sql, String.class, courseId);
        } catch (Exception e) {
            // 해당 ID의 강의가 없을 경우 예외 처리
            return "제목 없음 (ID: " + courseId + ")";
        }
    }

    // CourseDetails 객체 매핑
    private RowMapper<CourseDetails> courseDetailsRowMapper = new RowMapper<CourseDetails>() {
        @Override
        public CourseDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
            CourseDetails details = new CourseDetails();
            details.setCourses_name(rs.getString("courses_name"));
            details.setThumbnail_url(rs.getString("thumbnail_url"));
            return details;
        }
    };

    // 강의 ID를 기반으로 제목과 썸네일 URL을 동시에 조회합니다.
    public CourseDetails findCourseDetailsById(int courseId) {
        // courses_name과 thumbnail_url 두 컬럼을 조회
        String sql = "SELECT courses_name, thumbnail_url FROM courses WHERE id = ?";
        try {
            return jdbc.queryForObject(sql, courseDetailsRowMapper, courseId);
        } catch (Exception e) {
            // 해당 ID의 강의가 없을 경우 기본값 반환
            CourseDetails empty = new CourseDetails();
            empty.setCourses_name("삭제된 강의 (ID: " + courseId + ")");
            empty.setThumbnail_url("/images/default_thumbnail.jpg"); // 기본 썸네일 URL
            return empty;
        }
    }


}