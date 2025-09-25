package com.vegan.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notice {
    private int id;
    private String notice_name;
    private String notice_title;
    private String notice_content;
    private Date created_at;
}
