package com.community.course.quiz;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class QuizRepo {

    private final JdbcTemplate jdbc;

    public QuizRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private RowMapper<Quiz> quizRowMapper() {
        return (rs, rowNum) -> {
            return new Quiz(
                    rs.getLong("id"),
                    rs.getLong("courses_id"),
                    rs.getString("quiz_name"),
                    rs.getString("quiz_question"),
                    rs.getString("quiz_answer")
            );
        };
    }

    public Optional<Quiz> findById(long id) {
        String sql = "SELECT * FROM quiz WHERE id = ?";
        Quiz quiz = jdbc.queryForObject(sql, quizRowMapper(), id);
        return Optional.ofNullable(quiz);
    }


}
