package com.vegan.repository;

import com.vegan.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Repository
@RequiredArgsConstructor
public class PostRepository {
    private final JdbcTemplate jdbc;

    // 단일 게시글 조회 (id)
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

                // category: enum
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

    // 전체 게시글 조회
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

                // category: enum
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
    public void update(Post post) {
        String sql = "UPDATE posts SET title=?, content=?, author=?, fixed=?, category=? WHERE id=?";
        jdbc.update(sql,
                post.getTitle(),
                post.getContent(),
                post.getAuthor(),
                post.getFixed(),
                post.getCategory() != null ? post.getCategory().name() : null,
                post.getId());
    }

    // 새 글 등록
    public void save(Post post) {
        String sql = "INSERT INTO posts (title, content, author, fixed, category, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
        jdbc.update(sql,
                post.getTitle(),
                post.getContent(),
                post.getAuthor(),
                post.getFixed(),
                post.getCategory() != null ? post.getCategory().name() : null);
    }

    // --- ResultSet → Post 객체로 변환 ---
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
}


