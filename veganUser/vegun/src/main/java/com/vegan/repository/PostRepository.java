package com.vegan.repository;

import com.vegan.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepository {
    private final JdbcTemplate jdbc;

    // 1️⃣ 단일 게시글 조회 (id 기준)
    public Post findById(long id) {
        String sql = "SELECT * FROM posts WHERE id = ?";
        return jdbc.query(sql, new Object[]{id}, rs -> {
            if (rs.next()) {
                Post post = new Post();
                post.setId(rs.getLong("id"));
                post.setAuthor(rs.getString("author"));
                post.setTitle(rs.getString("title"));
                post.setContent(rs.getString("content"));
                post.setCreated_at(rs.getObject("created_at", LocalDateTime.class));

                // category: enum 변환
                String categoryStr = rs.getString("category");
                try {
                    post.setCategory(Locale.Category.valueOf(categoryStr));
                } catch (Exception e) {
                    post.setCategory(null);
                }

                post.setFixed(rs.getBoolean("fixed"));
                return post;
            }
            return null;
        });
    }

    // 2️⃣ 전체 게시글 조회
    public List<Post> findAll() {
        String sql = "SELECT * FROM posts ORDER BY created_at DESC";
        return jdbc.query(sql, rs -> {
            List<Post> posts = new ArrayList<>();
            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getLong("id"));
                post.setAuthor(rs.getString("author"));
                post.setTitle(rs.getString("title"));
                post.setContent(rs.getString("content"));
                post.setCreated_at(rs.getObject("created_at", LocalDateTime.class));

                String categoryStr = rs.getString("category");
                try {
                    post.setCategory(Locale.Category.valueOf(categoryStr));
                } catch (Exception e) {
                    post.setCategory(null);
                }

                post.setFixed(rs.getBoolean("fixed"));
                posts.add(post);
            }
            return posts;
        });
    }

    // 게시글 수정
    public Post update(Post post) {
        String sql = "UPDATE posts SET title = ?, content = ?, fixed = ?, category = ? WHERE id = ?";
        int rows = jdbc.update(
                sql,
                post.getTitle(),
                post.getContent(),
                post.getFixed(),
                post.getCategory() != null ? post.getCategory().name() : null,
                post.getId()
        );

        if (rows == 0) {
            throw new RuntimeException("수정할 게시글이 존재하지 않습니다. id=" + post.getId());
        }
        return post;
    }


    // 4️⃣ 새 글 등록
    public Post save(Post post) {
        String sql = "INSERT INTO posts (title, content, author, fixed, category, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
        jdbc.update(sql,
                post.getTitle(),
                post.getContent(),
                post.getAuthor(),
                post.getFixed(),
                post.getCategory() != null ? post.getCategory().name() : null);
        return post;
    }

    // 5️⃣ ResultSet → Post 객체 변환 (공통용, 필요 시)
    private RowMapper<Post> postRowMapper() {
        return (rs, rowNum) -> {
            Post post = new Post();
            post.setId(rs.getLong("id"));
            post.setTitle(rs.getString("title"));
            post.setContent(rs.getString("content"));
            post.setAuthor(rs.getString("author"));
            post.setCreated_at(rs.getObject("created_at", LocalDateTime.class));
            return post;
        };
    }
    public void deleteById(Long id) {
        String sql = "DELETE FROM posts WHERE id = ?";
        jdbc.update(sql, id);
    }
}



