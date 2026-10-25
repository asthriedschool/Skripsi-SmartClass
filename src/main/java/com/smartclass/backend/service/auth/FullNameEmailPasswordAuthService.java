package com.smartclass.backend.service.auth;

import com.smartclass.backend.config.JwtConfig;
import com.smartclass.backend.dto.AuthResponse;
import com.smartclass.backend.dto.LoginRequest;
import com.smartclass.backend.entity.UserEntity;
import com.smartclass.backend.repository.UserRepository;
import com.smartclass.backend.service.RefreshTokenService;
import com.smartclass.backend.util.JwtUtils;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.smartclass.backend.entity.RefreshTokenEntity;

import java.time.LocalDateTime;

import javax.crypto.SecretKey;

@Service
public class FullNameEmailPasswordAuthService implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;
    private final RefreshTokenService refreshTokenService;

    public FullNameEmailPasswordAuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            SecretKey secretKey,
            JwtConfig jwtConfig,
            RefreshTokenService refreshTokenService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Data login tidak boleh null"
            );
        }

        if (request.username() == null ||
                request.username().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Username wajib diisi"
            );
        }

        if (request.password() == null ||
                request.password().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Password wajib diisi"
            );
        }

        String email = request.username().trim();

        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        email,
                        request.password()
                )
        );

        UserEntity user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "User terautentikasi tidak ditemukan"
                ));

        user.setLast_login_at(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = JwtUtils.generateAccessToken(
                user.getFull_name(),
                user.getEmail(),
                user.getRole().name(),
                secretKey,
                jwtConfig.getExpirationTimeAccessToken()
        );

        String refreshToken = refreshTokenService.createRefreshToken(
                user.getId_user(),
                null,
                null
        );

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtConfig.getExpirationTimeAccessToken()
        );
    }

    @Override
    public AuthResponse refresh(String refreshToken) {

        if (refreshToken == null ||
                refreshToken.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Refresh token tidak boleh kosong"
            );
        }

        RefreshTokenEntity token =
                refreshTokenService.validateRefreshToken(
                        refreshToken
                );

        UserEntity user =
                userRepository.findById(token.getId_user())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User tidak ditemukan"
                                )
                        );

        String accessToken =
                JwtUtils.generateAccessToken(
                        user.getFull_name(),
                        user.getEmail(),
                        user.getRole().name(),
                        secretKey,
                        jwtConfig.getExpirationTimeAccessToken()
                );

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtConfig.getExpirationTimeAccessToken()
        );
    }
}
