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
import java.util.ArrayList;
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
        String sql = "INSERT INTO courses (courses_name, description, video_url, thumbnail_url, courses_category, total_sec) VALUES (?, ?, ?, ?, ?, ?)";
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
                return ps;
            }
        }, keyHolder);

        // 생성된 ID를 Course 객체에 다시 설정 (선택 사항)
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

    // 내 강의 페이지네이션
    public List<Course> findWithFilterAndPagination(int limit, int offset, String search, String category) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM courses");
        List<Object> params = new ArrayList<>();

        // 동적 WHERE 절 구성
        if (search != null && !search.isEmpty() && !category.equals("전체")) {
            sqlBuilder.append(" WHERE courses_name LIKE ? AND courses_category = ?");
            params.add("%" + search + "%");
            params.add(category);
        } else if (search != null && !search.isEmpty()) {
            sqlBuilder.append(" WHERE courses_name LIKE ?");
            params.add("%" + search + "%");
        } else if (!category.equals("전체")) {
            sqlBuilder.append(" WHERE courses_category = ?");
            params.add(category);
        }

        sqlBuilder.append(" LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        return jdbc.query(sqlBuilder.toString(), courseRowMapper(), params.toArray());
    }

    // 내 강의 검색
    public long countWithFilterAndSearch(String search, String category) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(*) FROM courses");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.isEmpty() && !category.equals("전체")) {
            sqlBuilder.append(" WHERE courses_name LIKE ? AND courses_category = ?");
            params.add("%" + search + "%");
            params.add(category);
        } else if (search != null && !search.isEmpty()) {
            sqlBuilder.append(" WHERE courses_name LIKE ?");
            params.add("%" + search + "%");
        } else if (!category.equals("전체")) {
            sqlBuilder.append(" WHERE courses_category = ?");
            params.add(category);
        }

        return jdbc.queryForObject(sqlBuilder.toString(), Long.class, params.toArray());
    }

    // 강의 삭제
    public void deleteById(int id) {
        String sql = "DELETE FROM courses WHERE id = ?";
        jdbc.update(sql, id);
    }


}
