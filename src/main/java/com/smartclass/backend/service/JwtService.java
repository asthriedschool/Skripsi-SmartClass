package com.smartclass.backend.service;

import com.smartclass.backend.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long accessExpirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration-ms:900000}") long accessExpirationMs
    ) {
        this.signingKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );

        this.accessExpirationMs = accessExpirationMs;
    }

    /**
     * Membuat access token JWT untuk user.
     */
    public String generateAccessToken(UserEntity user) {

        Date issuedAt = new Date();
        Date expiration = new Date(
                issuedAt.getTime() + accessExpirationMs
        );

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("id_user", user.getId_user())
                .claim("role", user.getRole().name())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(signingKey)
                .compact();
    }

    /**
     * Mengambil email/subject dari JWT.
     */
    public String extractEmail(String token) {

        return extractAllClaims(token)
                .getSubject();
    }

    /**
     * Mengecek apakah JWT valid dan belum expired.
     */
    public boolean isTokenValid(String token) {

        try {
            Claims claims = extractAllClaims(token);

            return claims.getExpiration()
                    .after(new Date());

        } catch (Exception e) {
            return false;
        }
    }

    public long getAccessExpirationSeconds() {
        return accessExpirationMs / 1000;
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}