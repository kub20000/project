package com.vegan.repository;


import com.vegan.entity.Role;
import com.vegan.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final JdbcTemplate jdbc;
    // 로그인
    public Optional<User> findByUsernameAndPassword(String username, String password) {
        // 1. username으로 DB 조회
        String sql = "SELECT * FROM users WHERE username = ?";

        try {
            User user = jdbc.queryForObject(sql, new UserRowMapper(), username);

            // 2. 평문 비밀번호 비교 (나중에 암호화 필요 시 passwordEncoder.matches() 사용)
            if (user.getPassword().equals(password)) {
                return Optional.of(user); // 로그인 성공
            } else {
                return Optional.empty(); // 비밀번호 불일치
            }

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty(); // username 없음
        }
    }
     //회원 저장
     public User save(User user) {
         System.out.println("==> user added");
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
                 "USER"  // 기본 역할 지정
         );

         if (result == 1) {
             System.out.println(result);
             System.out.println("회원 등록 성공");
             return user;
         } else {
             System.out.println("회원등록 실패");
             return null;
         }
     }
     //회원찾기 DB에서 username 기준으로 회원 조회
    public Optional<Object> findByUsername(String username) {
        System.out.println("==> user findByUserName :"+username);
        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            User user = jdbc.queryForObject(sql, new UserRowMapper(),username);
            System.out.println("user : "+user);
            return Optional.ofNullable(user);
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }
   //회원정보 수정
    public int update(User user) {
        String sql = "UPDATE users SET username=?, nickname=?, password=?, birthdate=?, phone=?, email=? WHERE id=?";
        return jdbc.update(sql,
                user.getUsername(),
                user.getNickname(),
                user.getPassword(),
                user.getBirthdate(),
                user.getPhone(),
                user.getEmail(),
                user.getId());
    }


    // ResultSet -> User 객체 변환
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
            user.setRole(Role.valueOf(rs.getString("role")));

            Timestamp ts = rs.getTimestamp("created_at");
            user.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);

            return user;
        }
    }
      // 회원 삭제
    public int delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbc.update(sql, id);
    }
     // 회원 ID로 조회
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbc.queryForObject(sql, new UserRowMapper(), id);
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    // 전체 회원 조회
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbc.query(sql, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setNickname(rs.getString("nickname"));
            user.setEmail(rs.getString("email"));
            return user;
        });
    }

    }









