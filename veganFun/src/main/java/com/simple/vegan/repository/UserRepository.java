package com.simple.vegan.repository;

import com.simple.vegan.entity.Role;
import com.simple.vegan.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    // ResultSet → User 매핑
    private User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setNickname(rs.getString("nickname"));
        user.setBirthdate(rs.getDate("birthdate") != null ? rs.getDate("birthdate").toLocalDate() : null);
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        String roleStr = rs.getString("role");
        user.setRole(roleStr != null ? Role.valueOf(roleStr) : Role.USER); // null이면 USER
        return user;
    }

    // 1. 전체 사용자 조회
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    // 2. ID로 조회(상세보기)
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE id=?";
        try {
            User user = jdbcTemplate.queryForObject(sql, this::mapRow, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    // username 조회
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username=?";
        try {
            User user = jdbcTemplate.queryForObject(sql, this::mapRow, username);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // 회원가입 저장
    public void save(User user) {
        String sql = "INSERT INTO users (id,username, password, nickname, birthdate, email, phone, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?,?)";
        jdbcTemplate.update(sql,
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getNickname() != null ? user.getNickname() : "", // null 방지
                user.getBirthdate(),
                user.getEmail(),
                user.getPhone(),
                user.getRole() != null ? user.getRole().name() : "USER" // null이면 USER로
        );
    }

    //회원 탈퇴
    public void deleteById(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    //회원 수정
    public void update(User user) { //db에서 전달받음
        String sql = "UPDATE users SET   email=?, phone=?  WHERE id=?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getPhone(),
                user.getId()
        );
    }

}

