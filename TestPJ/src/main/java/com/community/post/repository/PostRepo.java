package com.community.post.repository;

import com.community.post.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class PostRepo {
    private final JdbcTemplate jdbc;

    @Autowired
    public PostRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    //게시글 등록
    public Post add(Post post) {
        System.out.println("==> post added");
        String sql = "INSERT INTO posts (id, title, author, content, created_at) " +
                " VALUES (?,?,?,?,?)";;
        int result = jdbc.update(sql,
                post.getId(),
                post.getTitle(),
                post.getAuthor(),
                post.getContent(),
                post.getCreated_at()
        );
        if (result == 1) {
            System.out.println(result);
            System.out.println("게시글 등록 성공");
            return post;
        } else {
            System.out.println("게시글 등록 실패");
            return null;
        }
    }

    // 모든 게시글 출력
    public List<Post> findAll() {
        String sql = "SELECT * FROM posts";
        return jdbc.query(sql,postRowMapper());
        }

    // 게시글 매핑(출력 전 매핑)
    private RowMapper<Post> postRowMapper() {
        return (rs, rowNum) -> {
            java.sql.Timestamp timestamp = rs.getTimestamp("created_at");
            LocalDateTime createdAt = (timestamp != null) ? timestamp.toLocalDateTime() : null;
            return new Post(
                    rs.getInt("id"),
                    rs.getString("author"),
                    rs.getString("title"),
                    rs.getString("content"),
                    createdAt
            );
        };
    }

    // 특정 아이디 값의 데이터 가져오기
    public Optional<Post> findById(int id) {
        System.out.println("==> post findById" + id);
        String sql = "SELECT * FROM posts WHERE id = ?";
        Post post = jdbc.queryForObject(sql, postRowMapper(), id);
        System.out.println("==> post : " + id);
        return Optional.ofNullable(post);
    }

    // 게시글 삭제 (게시글이 없으면 삭제 불가)
    public void deleteById(int id) {
        String sql = "DELETE FROM posts WHERE id = ?";
        int rowAffected = jdbc.update(sql, id);
        if (rowAffected > 0) {
            System.out.println(rowAffected);
        } else {
            System.out.println("삭제할 게시글이 없습니다." + id);
        }
    }

    // 게시글 수정
    public void update(Post post) {
        String sql = "UPDATE posts SET title = ?, content = ? WHERE id = ?";
        jdbc.update(sql,
                post.getTitle(),
                post.getContent(),
                post.getId()
        );
    }

    // 다음글 가기 (id가 높은 것 중 가장 낮은 id)
    public Optional<Integer> findNextId(int postId) {
        String sql = "SELECT MIN(id) FROM posts WHERE id > ?";
        Integer nextId = jdbc.queryForObject(sql, Integer.class, postId);
        return Optional.ofNullable(nextId);
    }

    // 이전글 가기 (id가 낮은 것 중 가장 높은 id)
    public Optional<Integer> findPrevId(int postId) {
        String sql = "SELECT MAX(id) FROM posts WHERE id < ?";
        Integer prevId = jdbc.queryForObject(sql, Integer.class, postId);
        return Optional.ofNullable(prevId);
    }
}

