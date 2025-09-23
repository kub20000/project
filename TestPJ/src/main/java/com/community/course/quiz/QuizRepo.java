package com.community.course.quiz;

import com.community.post.entity.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class QuizRepo {

    private final JdbcTemplate jdbc;

    public QuizRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final class QuizRowMapper implements RowMapper<Quiz> {
        @Override
        public Quiz mapRow(ResultSet rs, int rowNum) throws SQLException {
            Quiz quiz = new Quiz();
            quiz.setId(rs.getLong("id"));
            quiz.setCoursesId(rs.getLong("coursesId"));
            quiz.setQuiz_name(rs.getString("quiz_name"));
            quiz.setQuiz_question(rs.getString("quiz_question"));
            quiz.setQuiz_answer(rs.getString("quiz_answer"));
            return quiz;
        }
    }

    public List<Quiz> findByCoursesId(long coursesId) {
        String sql = "SELECT * FROM quiz WHERE coursesId = ?";
        return jdbc.query(sql, new QuizRowMapper(), coursesId);
    }

    public Optional<Quiz> findOneByCoursesId(long coursesId) {
        String sql = "SELECT * FROM quiz WHERE coursesId = ?";
        Quiz quiz = jdbc.queryForObject(sql, new QuizRowMapper(), coursesId);
        return Optional.ofNullable(quiz);
    }
}
