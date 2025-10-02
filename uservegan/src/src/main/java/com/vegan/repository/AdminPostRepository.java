package com.vegan.repository;

import com.vegan.entity.Post;
import com.vegan.entity.User;
import jakarta.persistence.NamedNativeQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AdminPostRepository {
    private final JdbcTemplate jdbc;

        // 1️⃣ RowMapper: ResultSet → Post 객체 변환
        private final RowMapper<Post> mapper = (rs, rowNum) -> {
            Post post = new Post();
            post.setId(rs.getLong("id"));
            post.setTitle(rs.getString("title") != null ? rs.getString("title") : "");
            post.setContent(rs.getString("content") != null ? rs.getString("content") : "");
            post.setAuthor(rs.getString("author") != null ? rs.getString("author") : "Unknown");

            // 카테고리 안전 변환
            String categoryStr = rs.getString("category");
            Post.Category category = Post.Category.NOTICE;
            if (categoryStr != null) {
                try {
                    category = Post.Category.valueOf(categoryStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    category = Post.Category.NOTICE;
                }
            }
            post.setCategory(category);

            // 생성일
            Timestamp ts = rs.getTimestamp("created_at");
            post.setCreatedAt(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());

            // 고정 여부
            post.setFixed(rs.getInt("fixed"));

            return post;
        };// 2️⃣ 전체 게시글 조회
    public List<Post> findAll() {
        String sql = "SELECT * FROM posts ORDER BY created_at DESC";
        return jdbc.query(sql, mapper);
    }



        // 3️⃣ ID로 게시글 조회
        public Post findById(Long id) {
            String sql = "SELECT * FROM posts WHERE id = ?";
            try {
                return jdbc.queryForObject(sql, new Object[]{id}, mapper);
            } catch (EmptyResultDataAccessException e) {
                return null; // 없으면 null
            }
        }

        // 4️⃣ 게시글 저장 / 수정
        public void save(Post post) {
            System.out.println("updatesql");
            String sql = "UPDATE posts SET title = ?, content = ?, category = ?, fixed = ? WHERE id = ?";
            jdbc.update(sql,
                    post.getTitle(),
                    post.getContent(),
                    post.getCategory() != null ? post.getCategory().name() : "NOTICE",
                    post.getFixed(),
                    post.getId());
        }


}



















