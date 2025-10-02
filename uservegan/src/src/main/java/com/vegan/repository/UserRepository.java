package com.vegan.repository;


import com.vegan.entity.Role;
import com.vegan.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final JdbcTemplate jdbc;
    // 로그인: username + password
    // -----------------------
    public Optional<User> findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            User user = jdbc.queryForObject(sql, new UserRowMapper(), username);
            // 평문 비밀번호 비교 (나중에 암호화 가능)
            if (user.getPassword().equals(password)) {
                return Optional.of(user);
            } else {
                return Optional.empty();
            }
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // -----------------------
    // 회원가입: user 저장
    // -----------------------
    public User save(User user) {
        String sql = "INSERT INTO users " +
                "(name, username, password, nickname, birthdate, email, phone, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        int result = jdbc.update(sql,
                user.getName(),
                user.getUsername(),
                user.getPassword(),
                user.getNickname(),
                user.getBirthdate(),
                user.getEmail(),
                user.getPhone(),
                user.getRole() != null ? user.getRole().name() : "USER" // 기본 USER
        );

        if (result == 1) {
            System.out.println("회원 등록 성공: " + user.getUsername());
            return user;
        } else {
            System.out.println("회원등록 실패");
            return null;
        }
    }

    // -----------------------
    // 아이디로 조회 (중복 체크용)
    // -----------------------
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            User user = jdbc.queryForObject(sql, new UserRowMapper(), username);
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // -----------------------
    // 닉네임으로 조회 (중복 체크용)
    // -----------------------
    public Optional<User> findByNickname(String nickname) {
        String sql = "SELECT * FROM users WHERE nickname = ?";
        try {
            User user = jdbc.queryForObject(sql, new UserRowMapper(), nickname);
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // -----------------------
    // 회원 정보 수정
    // -----------------------
    public void update(User user) {
        String sql = "UPDATE users SET username = ?,name=?, nickname = ?, password = ?, birthdate = ?, phone = ?, email = ?,role=? WHERE id = ?";
        jdbc.update(sql,
                user.getUsername(),
                user.getName(),
                user.getNickname(),
                user.getPassword(),
                user.getBirthdate() != null ? Date.valueOf(user.getBirthdate()) : null,
                user.getPhone(),
                user.getEmail(),
                user.getRole().name(),
                user.getId()
        );
    }

    // -----------------------
    // 회원 삭제
    // -----------------------
    public int delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbc.update(sql, id);
    }

    // -----------------------
    // 회원 ID로 조회
    // -----------------------
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbc.queryForObject(sql, new UserRowMapper(), id);
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // -----------------------
    // 전체 회원 조회
    // -----------------------
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbc.query(sql, new UserRowMapper());
    }

    // -----------------------
    // ResultSet -> User 객체 변환
    // -----------------------
    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setNickname(rs.getString("nickname"));
            user.setEmail(rs.getString("email"));
            user.setPhone(rs.getString("phone"));
            user.setRole(Role.valueOf(rs.getString("role"))); // DB 문자열 -> enum

            // 🔹 생일 추가
            java.sql.Date birth = rs.getDate("birthdate");
            user.setBirthdate(birth != null ? birth.toLocalDate() : null);

            Timestamp ts = rs.getTimestamp("created_at");
            user.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);

            return user;
        }
    }
}









