package com.smartclass.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(
        name = "tb_refresh_tokens",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_refresh_token_hash",
                        columnNames = "token_hash"
                )
        }
)
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_refresh_token")
    private Long id_refresh_token;

    @Column(name = "id_user", nullable = false)
    private Long id_user;

    @Column(
            name = "token_hash",
            nullable = false,
            length = 64,
            unique = true
    )
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expires_at;

    @Column(name = "revoked_at")
    private LocalDateTime revoked_at;

    @Column(name = "user_agent", length = 255)
    private String user_agent;

    @Column(name = "ip_address", length = 45)
    private String ip_address;

    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime created_at;
}