package com.smartclass.backend.service;

import com.smartclass.backend.entity.RefreshTokenEntity;
import com.smartclass.backend.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // Membuat refresh token baru
    public String createRefreshToken(
            Long id_user,
            String userAgent,
            String ipAddress
    ) {

        if (id_user == null) {
            throw new IllegalArgumentException(
                    "ID user tidak boleh null"
            );
        }

        // Generate token asli
        String rawToken = generateToken();

        // Hash token sebelum disimpan ke database
        String tokenHash = hashToken(rawToken);

        // Buat entity
        RefreshTokenEntity refreshToken = new RefreshTokenEntity();

        refreshToken.setId_user(id_user);

        // Field Java = tokenHash
        // Kolom database tetap = token_hash
        refreshToken.setTokenHash(tokenHash);

        refreshToken.setExpires_at(
                LocalDateTime.now()
                        .plusNanos(refreshExpirationMs * 1_000_000)
        );

        refreshToken.setRevoked_at(null);
        refreshToken.setUser_agent(userAgent);
        refreshToken.setIp_address(ipAddress);

        // Simpan hash ke MySQL
        refreshTokenRepository.save(refreshToken);

        // Yang dikirim ke client adalah token asli
        return rawToken;
    }

    // Memeriksa refresh token
    public RefreshTokenEntity validateRefreshToken(
            String rawToken
    ) {

        if (rawToken == null || rawToken.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Refresh token tidak boleh kosong"
            );
        }

        // Hash token yang dikirim client
        String tokenHash = hashToken(rawToken);

        // Cari berdasarkan hash
        RefreshTokenEntity refreshToken =
                refreshTokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Refresh token tidak ditemukan"
                                )
                        );

        // Cek apakah token sudah dicabut
        if (refreshToken.getRevoked_at() != null) {
            throw new RuntimeException(
                    "Refresh token sudah tidak aktif"
            );
        }

        // Cek apakah token sudah expired
        if (refreshToken.getExpires_at()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Refresh token sudah expired"
            );
        }

        return refreshToken;
    }

    // Mencabut refresh token
    public void revokeRefreshToken(
            String rawToken
    ) {

        if (rawToken == null || rawToken.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Refresh token tidak boleh kosong"
            );
        }

        // Hash token yang dikirim client
        String tokenHash = hashToken(rawToken);

        // Cari refresh token berdasarkan hash
        RefreshTokenEntity refreshToken =
                refreshTokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Refresh token tidak ditemukan"
                                )
                        );

        // Tandai token sebagai revoked
        refreshToken.setRevoked_at(
                LocalDateTime.now()
        );

        // Simpan perubahan
        refreshTokenRepository.save(refreshToken);
    }

    // Generate token random
    private String generateToken() {

        byte[] randomBytes = new byte[64];

        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    // SHA-256 hash
    private String hashToken(String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            token.getBytes(StandardCharsets.UTF_8)
                    );

            StringBuilder hexString =
                    new StringBuilder();

            for (byte b : hash) {

                String hex =
                        Integer.toHexString(
                                0xff & b
                        );

                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "SHA-256 tidak tersedia",
                    e
            );
        }
    }
}