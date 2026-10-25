package com.smartclass.backend.config;

import com.smartclass.backend.security.BearerAuthProvider;
import com.smartclass.backend.security.CustomAuthEntryPoint;
import com.smartclass.backend.security.JwtAuthFilter;

import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.List;


@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthFilter jwtAuthFilter
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )

                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                customAuthEntryPoint()
                        )
                )

                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(
            @Qualifier("daoAuthenticationProvider")
            AuthenticationProvider daoAuthenticationProvider,

            @Qualifier("bearerAuthProvider")
            AuthenticationProvider bearerAuthProvider
    ) {

        final List<AuthenticationProvider> authenticationProviders =
                List.of(
                        daoAuthenticationProvider,
                        bearerAuthProvider
                );

        final var providerManager =
                new ProviderManager(authenticationProviders);

        return providerManager;
    }


    @Bean("daoAuthenticationProvider")
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService
    ) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }


    @Bean
    public SecretKey secretKey(JwtConfig jwtConfig) {

        return Keys.hmacShaKeyFor(
                jwtConfig
                        .getSecretKey()
                        .getBytes(StandardCharsets.UTF_8)
        );
    }


    @Bean
    public CustomAuthEntryPoint customAuthEntryPoint() {
        return new CustomAuthEntryPoint();
    }
}