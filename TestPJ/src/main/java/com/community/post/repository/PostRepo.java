package com.bproject.post.repository;

import com.bproject.course.entity.Course;
import com.bproject.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepo {
    private final JdbcTemplate jdbc;

    //게시글 등록
    public Post add(Post post) {
        System.out.println("==> post added");
        String sql = "INSERT INTO posts (id, author, title, content, created_at, category, fixed) " +
                " VALUES (?,?,?,?,?,?,?)";;
        int result = jdbc.update(sql,
                post.getId(),
                post.getAuthor(),
                post.getTitle(),
                post.getContent(),
                post.getCreated_at(),
                post.getCategory().name().toUpperCase(),
                post.getFixed()
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

            // category 값 처리: null 값 체크 및 기본값 지정
            String categoryString = rs.getString("category");
            Post.Category categoryEnum = (categoryString != null)
                    ? Post.Category.valueOf(categoryString.toUpperCase())
                    : Post.Category.FREEBOARD;

            Post p = new Post();
            p.setId(rs.getInt("id"));
            p.setAuthor(rs.getString("author"));
            p.setTitle(rs.getString("title"));
            p.setContent(rs.getString("content"));
            p.setCreated_at(createdAt);
            p.setCategory(categoryEnum);
            p.setFixed(rs.getBoolean("fixed"));
            return p;
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
        String sql = "UPDATE posts SET title = ?, content = ?, category = ?, fixed = ? WHERE id = ?";
        jdbc.update(sql,
                post.getTitle(),
                post.getContent(),
                post.getCategory().name().toUpperCase(),
                post.getFixed(),
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

    // 페이지네이션
    public List<Post> getPostsWithPagination(int limit, int offset) {
        String sql = "SELECT id, author, title, content, created_at, category, fixed FROM posts ORDER BY created_at DESC LIMIT ? OFFSET ?";
        return jdbc.query(sql, postRowMapper(), limit, offset);
    }

    public int getTotalPostsCount() {
        String sql = "SELECT COUNT(*) FROM posts";
        return jdbc.queryForObject(sql, Integer.class);
    }

    // 마이페이지 게시글 조회
    public List<Post> findMyPostsWithPagination(String authorNickname, int limit, int offset, String keyword) {
        // SQL 빌더를 사용하여 동적으로 쿼리를 구성합니다.
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM posts WHERE author = ?");
        List<Object> params = new ArrayList<>();
        params.add(authorNickname); // 1. 닉네임 필터링 (필수)

        // 2. 검색 키워드 필터링 (선택)
        if (keyword != null && !keyword.isEmpty()) {
            sqlBuilder.append(" AND title LIKE ?"); // 제목만 검색한다고 가정
            params.add("%" + keyword + "%");
        }

        // 3. 정렬 및 페이지네이션
        sqlBuilder.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        return jdbc.query(sqlBuilder.toString(), postRowMapper(), params.toArray());
    }

    // 마이페이지 게시글 카운트, 검색
    public long countMyPosts(String authorNickname, String keyword) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(*) FROM posts WHERE author = ?");
        List<Object> params = new ArrayList<>();
        params.add(authorNickname);

        // 검색 키워드 필터링
        if (keyword != null && !keyword.isEmpty()) {
            sqlBuilder.append(" AND title LIKE ?");
            params.add("%" + keyword + "%");
        }

        // 결과는 Long 타입으로 받습니다.
        return jdbc.queryForObject(sqlBuilder.toString(), Long.class, params.toArray());
    }


}
