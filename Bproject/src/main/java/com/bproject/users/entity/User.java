package com.bproject.users.entity;


import lombok.*;

import javax.management.relation.Role;
import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String name;
    private String username;
    private String password;
    private String nickname;
    private LocalDate birthdate;
    private String email;
    private String phone;
    private String role;
    private Timestamp created_at;
}
