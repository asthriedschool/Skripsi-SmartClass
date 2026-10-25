package com.smartclass.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    private Long id_user;
    private String full_name;
    private String email;
    private String password;
    private String role;
    private String profile_picture;
    private String account_status;
    private LocalDateTime last_login_at;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
