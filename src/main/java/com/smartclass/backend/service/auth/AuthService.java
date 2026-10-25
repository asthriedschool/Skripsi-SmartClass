package com.smartclass.backend.service.auth;

import com.smartclass.backend.dto.AuthResponse;
import com.smartclass.backend.dto.LoginRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);

    AuthResponse refresh(String refreshToken);
}
