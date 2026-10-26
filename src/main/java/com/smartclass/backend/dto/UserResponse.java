package com.smartclass.backend.dto;

import com.smartclass.backend.entity.enumsUserEntity.AccountStatus;
import com.smartclass.backend.entity.enumsUserEntity.Role;

import java.time.LocalDateTime;

public record UserResponse(

        Long id_user,
        String full_name,
        String email,
        Role role,
        AccountStatus account_status,
        String profile_picture,
        LocalDateTime last_login_at,
        LocalDateTime created_at,
        LocalDateTime updated_at

) {
}