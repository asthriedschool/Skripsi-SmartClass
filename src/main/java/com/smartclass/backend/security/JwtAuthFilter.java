package com.smartclass.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;

import org.springframework.util.StringUtils;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;


    public JwtAuthFilter(
            AuthenticationManager authenticationManager
    ) {
        this.authenticationManager = authenticationManager;
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {


        //Extract authorization header
        final String requestHeader =
                request.getHeader("Authorization"); //Bearer <token>


        final String token =
                extractToken(requestHeader);


        //if token is empty
        if (token == null || token.isBlank()) {

            filterChain.doFilter(request, response);

            return;
        }


        try {

            //if we have bearer token
            final var unauthenticatedToken =
                    BearerAuthToken.unauthenticated(token);


            final Authentication authenticatedToken =
                    authenticationManager.authenticate(
                            unauthenticatedToken
                    );


            SecurityContextHolder.getContext()
                    .setAuthentication(authenticatedToken);


        } catch (AuthenticationException e) {

            // Token tidak valid.
            // Biarkan request lanjut tanpa authentication.
            // Endpoint protected akan menghasilkan 401
            // melalui CustomAuthEntryPoint.

            SecurityContextHolder.clearContext();
        }


        filterChain.doFilter(request, response);
    }


    private String extractToken(
            String authorizationHeader
    ) {

        if (!StringUtils.hasText(authorizationHeader)) {
            return null;
        }


        if (!authorizationHeader.startsWith(
                BearerAuthToken.TOKEN_TYPE + " "
        )) {
            return null;
        }


        return authorizationHeader
                .substring(
                        BearerAuthToken.TOKEN_TYPE.length()
                                + 1
                )
                .trim();
    }
}