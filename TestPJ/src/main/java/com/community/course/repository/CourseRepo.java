package com.community.course.repository;

import com.community.course.entity.Course;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;


@Repository
public class CourseRepo {

    private final JdbcTemplate jdbc;

    public CourseRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
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
            c.setDuration_sec(rs.getInt("duration_sec"));
            c.setLike_count(rs.getInt("like_count"));
            c.setTotal_sec(rs.getInt("total_sec"));
            c.setThumbnail_url(rs.getString("thumbnail_url"));
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
        String sql = "INSERT INTO courses (courses_name, description, video_url, thumbnail_url, courses_category) VALUES (?, ?, ?, ?, ?)";
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
                return ps;
            }
        }, keyHolder);

        // 생성된 ID를 Course 객체에 다시 설정 (선택 사항)
        course.setId(keyHolder.getKey().intValue());
    }


}
