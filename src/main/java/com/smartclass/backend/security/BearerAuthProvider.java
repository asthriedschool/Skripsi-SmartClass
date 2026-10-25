package com.smartclass.backend.security;

import com.smartclass.backend.util.JwtUtils;

import io.jsonwebtoken.Claims;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.util.List;


@Component("bearerAuthProvider")
@RequiredArgsConstructor
public class BearerAuthProvider implements AuthenticationProvider {

    private final SecretKey secretKey;


    @Override
    public Authentication authenticate(
            Authentication authentication
    ) throws AuthenticationException {

        if (!(authentication instanceof BearerAuthToken authToken)) {
            return null;
        }

        String token = (String) authToken.getCredentials();

        if (token == null || token.isBlank()) {
            throw new BadCredentialsException(
                    "Bearer token tidak boleh kosong"
            );
        }


        try {

            final Claims claims =
                    JwtUtils.parseToken(
                            token,
                            secretKey
                    );


            final String email =
                    claims.getSubject();

            if (email == null || email.isBlank()) {
                throw new BadCredentialsException(
                        "Subject JWT tidak ditemukan"
                );
            }


            final List<? extends GrantedAuthority> roles =
                    JwtUtils.extractAuthorities(claims);


            return BearerAuthToken.authenticated(
                    email,
                    roles
            );

        } catch (AuthenticationException e) {

            throw e;

        } catch (Exception e) {

            throw new BadCredentialsException(
                    "JWT tidak valid atau sudah expired",
                    e
            );
        }
    }


    @Override
    public boolean supports(Class<?> authentication) {

        return BearerAuthToken.class.isAssignableFrom(
                authentication
        );
    }
}