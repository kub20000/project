package com.vegan.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;
    private String name;
    private String username;
    private String password;
    private String nickname;
    private LocalDate birthdate;
    private String email;
    private String phone;
    @Enumerated(EnumType.STRING)
    private Role role;  // DB 값이 "ADMIN", "USER", "STUDENT", "INSTRUCTOR"이면 자동 매핑
    private LocalDateTime createdAt; // 생성일

    public void setRoleFromString(String roleStr) {
        if (roleStr != null) {
            this.role = Role.valueOf(roleStr.toUpperCase()); // 소문자도 안전하게 처리
        }
    }




}
