package com.club.entity;

import lombok.Data;

@Data
public class LoginVO {
    private Long id;
    private String username;
    private String name;
    private String role;
    private String avatarColor;
    private String token;
}