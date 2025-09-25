package com.vegan.repository;

import com.vegan.entity.Post;
import com.vegan.entity.Role;
import com.vegan.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbc;
    // 👉 ID로 사용자 찾기
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbc.query(sql, new Object[]{id}, (rs, rowNum) -> mapRow(rs));
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    // 👉 username으로 사용자 찾기
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        List<User> users = jdbc.query(sql, new Object[]{username}, (rs, rowNum) -> mapRow(rs));
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    // 👉 전체 사용자 조회
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbc.query(sql, (rs, rowNum) -> mapRow(rs));
    }

    // 👉 username 중복 체크
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    // 👉 회원 저장
    public int save(User user) {
        String sql = "INSERT INTO users (username, name, password, nickname, email, phone, birthdate, role, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        return jdbc.update(sql,
                user.getUsername(),
                user.getName(),
                user.getPassword(),
                user.getNickname(),
                user.getEmail(),
                user.getPhone(),
                user.getBirthdate(),
                user.getRole() != null ? user.getRole().name() : "USER"
        );
    }

    // 👉 사용자 정보 수정
    public int updateUser(User user) {
        String sql = "UPDATE users SET name=?, password=?, nickname=?, email=?, phone=?, birthdate=? WHERE id=?";
        return jdbc.update(sql,
                user.getName(),
                user.getPassword(),
                user.getNickname(),
                user.getEmail(),
                user.getPhone(),
                user.getBirthdate(),
                user.getId()
        );
    }

    // 👉 비밀번호 변경
    public int updatePassword(int id, String newPassword) {
        String sql = "UPDATE users SET password=? WHERE id=?";
        return jdbc.update(sql, newPassword, id);
    }

    // 👉 회원 삭제
    public int deleteByUsername(String username) {
        String sql = "DELETE FROM users WHERE username=?";
        return jdbc.update(sql, username);
    }


    // ✅ DB → User 객체 매핑 함수
    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setName(rs.getString("name"));
        user.setNickname(rs.getString("nickname"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setBirthdate(rs.getObject("birthdate", LocalDate.class));

        // ✅ Role 안전 처리
        String roleStr = rs.getString("role");
        try {
            user.setRole(Role.valueOf(roleStr.toUpperCase())); // 대문자로 변환
        } catch (IllegalArgumentException e) {
            user.setRole(Role.USER); // 잘못된 값이면 기본 USER
        }

        user.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        return user;
    }
    public int deleteById(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbc.update(sql, id); // 삭제된 행 수 반환
    }
}








