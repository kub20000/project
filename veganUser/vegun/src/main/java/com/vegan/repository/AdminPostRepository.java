package com.vegan.repository;

import com.vegan.entity.Post;
import com.vegan.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
@RequiredArgsConstructor
public class AdminPostRepository {
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
        // 기존 게시글 조회
        Post existing = findById(post.getId());
        if (existing == null)
            throw new RuntimeException("수정할 게시글이 존재하지 않습니다. id=" + post.getId());

        // 수정할 필드만 적용
        existing.setTitle(post.getTitle());
        existing.setContent(post.getContent());
        // fixed, category, author는 기존 값 유지
        Boolean fixedValue = existing.getFixed() != null ? existing.getFixed() : false;
        String categoryValue = existing.getCategory() != null ? existing.getCategory().name() : null;
        String authorValue = existing.getAuthor(); // 외래키 보호

        String sql = "UPDATE posts SET title = ?, content = ?, fixed = ?, category = ?, author = ? WHERE id = ?";
        int rows = jdbc.update(
                sql,
                existing.getTitle(),
                existing.getContent(),
                fixedValue,
                categoryValue,
                authorValue,
                existing.getId()
        );

        if (rows == 0)
            throw new RuntimeException("수정할 게시글이 존재하지 않습니다. id=" + post.getId());

        return existing;
    }

    // 4️⃣ 새 글 등록
    public Post save(Post post, User loginUser) {
        String sql = "INSERT INTO posts (title, content, author, fixed, category, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
        System.out.println("post"+post);
        jdbc.update(
                sql,
                post.getTitle(),
                post.getContent(),
                loginUser.getNickname(),
                post.getFixed() != null ? post.getFixed() : false,
                post.getCategory() != null ? post.getCategory().name() : null
        );
        // 마지막 insert id 조회
        Long id = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        post.setId(id);
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



