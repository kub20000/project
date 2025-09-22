package com.bproject.users.repository;

import com.bproject.users.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> User.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .username(rs.getString("username"))
            .password(rs.getString("password"))
            .nickname(rs.getString("nickname"))
            .birthdate(rs.getDate("birthdate").toLocalDate())
            .email(rs.getString("email"))
            .phone(rs.getString("phone"))
            .role(rs.getString("role"))
            .created_at(rs.getTimestamp("created_at"))
            .build();

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        return jdbcTemplate.query(sql, userRowMapper, username).stream().findFirst();
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return cnt != null && cnt > 0;
    }

    public long save(User user) {
        String sql = """
                INSERT INTO users(name, username, password, nickname, birthdate, email, phone, role)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql,
                user.getName(),
                user.getUsername(),
                user.getPassword(),
                user.getNickname(),
                Date.valueOf(user.getBirthdate()),
                user.getEmail(),
                user.getPhone(),
                user.getRole() == null ? "USER" : user.getRole()
        );
        // 방금 INSERT한 id 조회
        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        return id != null ? id : -1L;
    }

    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.query(sql, userRowMapper, id).stream().findFirst();
    }

    public int updatePasswordById(Long id, String hashed) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        return jdbcTemplate.update(sql, hashed, id);
    }
}
