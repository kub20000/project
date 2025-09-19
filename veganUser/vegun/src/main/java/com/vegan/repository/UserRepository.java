package com.vegan.repository;

import com.vegan.entity.Role;
import com.vegan.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbc;

    //로그인 사용자 이름으로 조회
    public Optional<User> findByUsername(String username) {
            String sql = "SELECT * FROM users WHERE username = ?";

            List<User> users = jdbc.query(
                    sql,
                    new Object[]{username}, // SQL ?에 들어갈 값
                    (rs, rowNum) -> {      // RowMapper: ResultSet -> User 객체
                        User user = new User();
                        user.setId(rs.getInt("id"));
                        user.setUsername(rs.getString("username"));
                        user.setPassword(rs.getString("password"));
                        user.setNickname(rs.getString("nickname"));
                        user.setBirthdate(rs.getDate("birthdate").toLocalDate());
                        user.setEmail(rs.getString("email"));
                        user.setPhone(rs.getString("phone"));
                        user.setRole(Role.valueOf(rs.getString("role"))); // role을 enum으로 변환
                        return user;
                    }
            );

            // 조회 결과가 없으면 Optional.empty(), 있으면 첫 번째 User 반환
            if (users.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(users.get(0));
            }
        }

    // username으로 삭제
    public void deleteByUsername(String username){
        String sql = "DELETE FROM users WHERE username = ?";
        int rowAffected = jdbc.update(sql, username);
        if (rowAffected > 0) {
            System.out.println("삭제된 사용자 수: " + rowAffected);
        } else {
            System.out.println("삭제할 사용자가 없습니다. username=" + username);
        }
    }
    //회원 수정
    public void updateUser(User user) {
        String sql = "UPDATE users SET username=?, nickname=?, password=?, birthdate=?, phone=?, email=? WHERE id=?";
        jdbc.update(sql,
                user.getUsername(),
                user.getNickname(),
                user.getPassword(),
                user.getBirthdate(),
                user.getPhone(),
                user.getEmail(),
                user.getId()
        );
    }
    // 비밀번호 변경
    public int updatePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        return jdbc.update(sql, newPassword, userId);
    }

    // id로 사용자 조회 (로그인/세션 확인용)
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbc.queryForObject(sql, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setNickname(rs.getString("nickname"));
            user.setEmail(rs.getString("email"));
            user.setPhone(rs.getString("phone"));
            return user;
        }, id);
    }
    // 사용자 등록
    public int save(User user) {
        String sql = "INSERT INTO users(username, password, nickname, email, phone, birthdate) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbc.update(sql,
                user.getUsername(),
                user.getPassword(),
                user.getNickname(),
                user.getEmail(),
                user.getPhone(),
                user.getBirthdate());
    }

    // 아이디 중복 체크
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    }



