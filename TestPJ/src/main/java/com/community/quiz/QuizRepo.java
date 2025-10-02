package com.bproject.quiz;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

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
            quiz.setQuiz_result(rs.getString("quiz_result"));
            return quiz;
        }
    }

    public List<Quiz> findByCoursesId(long coursesId) {
        String sql = "SELECT * FROM quiz WHERE coursesId = ?";
        return jdbc.query(sql, new QuizRowMapper(), coursesId);
    }

    public void saveUserAnswers(List<Quiz> submittedQuizzes) {
        String sql = "UPDATE quiz SET quiz_answer = ? WHERE id = ?";
        // 배치 업데이트를 사용하여 여러 퀴즈의 사용자 답안을 한 번에 효율적으로 저장합니다.
        jdbc.batchUpdate(sql, submittedQuizzes, submittedQuizzes.size(),
                (ps, quiz) -> {
                    ps.setString(1, quiz.getQuiz_answer());
                    ps.setLong(2, quiz.getId());
                });
    }

    private final RowMapper<Quiz> quizRowMapper = (rs, rowNum) -> {
        Quiz quiz = new Quiz();
        quiz.setId(rs.getLong("id"));
        quiz.setCoursesId(rs.getLong("coursesId"));
        quiz.setQuiz_name(rs.getString("quiz_name"));
        quiz.setQuiz_question(rs.getString("quiz_question"));
        quiz.setQuiz_answer(rs.getString("quiz_answer"));
        quiz.setQuiz_result(rs.getString("quiz_result")); // DB의 정답
        return quiz;
    };

    public List<Quiz> findQuizzesByIds(List<Long> quizIds) {
        // IN 절에 사용할 쉼표로 구분된 문자열 생성
        String inSql = String.join(",", "null", quizIds.toString()).replace("[", "").replace("]", "");
        String sql = "SELECT * FROM quiz WHERE id IN (" + inSql + ")";
        return jdbc.query(sql, quizRowMapper);
    }

    public long save(Quiz quiz) {
        String sql = "INSERT INTO quiz (coursesId, quiz_name, quiz_question, quiz_result) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, quiz.getCoursesId());
            ps.setString(2, quiz.getQuiz_name());
            ps.setString(3, quiz.getQuiz_question());
            ps.setString(4, quiz.getQuiz_result());
            return ps;
        }, keyHolder);

        // 생성된 ID 반환
        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : -1;
    }

    // 퀴즈 수정
    public void updateQuizzes(List<Quiz> quizzes) {
        String sql = "UPDATE quiz SET quiz_question = ?, quiz_result = ? WHERE id = ?";
        jdbc.batchUpdate(sql, quizzes, quizzes.size(),
                (ps, quiz) -> {
                    ps.setString(1, quiz.getQuiz_question());
                    ps.setString(2, quiz.getQuiz_result());
                    ps.setLong(3, quiz.getId());
                });
    }



}