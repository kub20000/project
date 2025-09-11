package com.community.post.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CommentRepo {
    private final JdbcTemplate jdbc;

    @Autowired
    public CommentRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private RowMapper<Comment> commentRowMapper() {
        return (rs, rowNum) -> {
            java.sql.Timestamp timestamp = rs.getTimestamp("created_at");
            LocalDateTime createdAt = (timestamp != null) ? timestamp.toLocalDateTime() : null;
            return new Comment(
                    rs.getInt("id"),
                    rs.getInt("posts_id"),
                    rs.getString("comments_name"),
                    rs.getString("comments_content"),
                    createdAt
            );
        };
    }

    public List<Comment> findById(int id) {
        String sql = "SELECT * FROM comments WHERE posts_id = ?";
        return jdbc.query(sql, commentRowMapper(), id);
    }

    public void deleteById(int id) {
        String sql = "DELETE FROM comments WHERE id = ?";
        int rowAffected = jdbc.update(sql, id);
        if (rowAffected > 0) {
            System.out.println(rowAffected);
        } else {
            System.out.println("삭제할 댓글이 없습니다." + id);
        }
    }
}
