package com.vegan.entity;

public enum Role {
    ADMIN,
    USER,
    STUDENT,
    INSTRUCTOR;

    // DB/프론트에서 넘어오는 문자열을 안전하게 매핑
    public static Role fromString(String value) {
        if (value == null) return null;
        return Role.valueOf(value.toUpperCase()); // 대소문자 상관없이 변환
    }
}
