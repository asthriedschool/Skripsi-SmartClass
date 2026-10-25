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
public class RefreshTokenModel {
    private Long id_refresh_token;
    private Long id_user;
    private String token_hash;
    private LocalDateTime expires_at;
    private LocalDateTime revoked_at;
    private String user_agent;
    private String ip_address;
    private LocalDateTime created_at;
}
