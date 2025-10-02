package com.bproject.post.comment;

import com.bproject.post.entity.Post;
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
            System.out.println("댓글 삭제"+rowAffected);
        } else {
            System.out.println("삭제할 댓글이 없습니다." + id);
        }
    }

    public Comment add(Comment comment) {
        System.out.println("==> comment added");
        String sql = "INSERT INTO comments (id, posts_id, comments_name, comments_content, created_at) " +
                " VALUES (?,?,?,?,?)";;
        int result = jdbc.update(sql,
                comment.getId(),
                comment.getPosts_id(),
                comment.getComments_name(),
                comment.getComments_content(),
                comment.getCreated_at()
        );
        if (result == 1) {
            System.out.println(result);
            System.out.println("댓글 등록 성공");
            return comment;
        } else {
            System.out.println("댓글 등록 실패");
            return null;
        }
    }

    public void update(Comment comment) {
        String sql = "UPDATE comments SET comments_content = ? WHERE id = ?";
        jdbc.update(sql,
                comment.getComments_content(),
                comment.getId()
        );
    }

    public void deleteByPostsId(int postsId) {
        String sql = "DELETE FROM comments WHERE posts_id = ?";
        int rowAffected = jdbc.update(sql, postsId);
        System.out.println("게시판 댓글 삭제"+rowAffected);
    }
}