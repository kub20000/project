package com.bproject.userscourses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersCourses {
    private int id;
    private int courses_id;
    private int users_id;
    private int progress;
    private int duration_sec;
}