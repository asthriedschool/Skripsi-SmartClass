package com.smartclass.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public final class JwtUtils {


    private JwtUtils() {
    }


    public static String generateAccessToken(
            String fullName,
            String email,
            String role,
            SecretKey secretKey,
            long expirationTimeSeconds
    ) {

        Instant now =
                Instant.now();


        Instant expiration =
                now.plusSeconds(
                        expirationTimeSeconds
                );


        return Jwts.builder()

                .subject(email)

                .claim(
                        "fullName",
                        fullName
                )

                .claim(
                        "role",
                        role
                )

                .issuedAt(
                        Date.from(now)
                )

                .expiration(
                        Date.from(expiration)
                )

                .signWith(secretKey)

                .compact();
    }


    public static Claims parseToken(
            String token,
            SecretKey secretKey
    ) {

        return Jwts.parser()

                .verifyWith(secretKey)

                .build()

                .parseSignedClaims(token)

                .getPayload();
    }


    public static String extractEmail(
            String token,
            SecretKey secretKey
    ) {

        Claims claims =
                parseToken(
                        token,
                        secretKey
                );


        return claims.getSubject();
    }


    public static List<? extends GrantedAuthority>
    extractAuthorities(
            Claims claims
    ) {

        String role =
                claims.get(
                        "role",
                        String.class
                );


        if (role == null ||
                role.isBlank()) {

            throw new IllegalArgumentException(
                    "Role tidak ditemukan di JWT"
            );
        }


        List<GrantedAuthority> authorities =
                new ArrayList<>();


        authorities.add(
                new SimpleGrantedAuthority(
                        "ROLE_" + role
                )
        );


        return authorities;
    }


    public static Claims getClaimsFromToken(
            String token,
            SecretKey secretKey
    ) {

        return parseToken(
                token,
                secretKey
        );
    }


    public static boolean isTokenValid(
            String token,
            SecretKey secretKey
    ) {

        try {

            parseToken(
                    token,
                    secretKey
            );

            return true;

        } catch (Exception e) {

            return false;
        }
    }
}