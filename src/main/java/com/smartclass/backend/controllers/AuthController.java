package com.smartclass.backend.controllers;

import com.smartclass.backend.dto.APIResponse;
import com.smartclass.backend.dto.AuthResponse;
import com.smartclass.backend.dto.LoginRequest;
import com.smartclass.backend.dto.RefreshTokenRequest;
import com.smartclass.backend.dto.RegisterRequest;
import com.smartclass.backend.service.UserService;
import com.smartclass.backend.service.auth.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;


    public AuthController(
            UserService userService,
            AuthService authService
    ) {
        this.userService = userService;
        this.authService = authService;
    }


    @PostMapping("/register")
    public ResponseEntity<APIResponse> registerUser(
            @RequestBody RegisterRequest request
    ) {

        userService.registerUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new APIResponse(
                                HttpStatus.CREATED.value(),
                                "Registrasi berhasil"
                        )
                );
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request
    ) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }


    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @RequestBody RefreshTokenRequest request
    ) {

        final var authResponse =
                authService.refresh(
                        request.refreshToken()
                );

        return ResponseEntity.ok(
                authResponse
        );
    }
}