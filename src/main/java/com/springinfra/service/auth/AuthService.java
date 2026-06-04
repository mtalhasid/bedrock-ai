package com.springinfra.service.auth;

import com.springinfra.dto.response.AuthResponse;
import com.springinfra.dto.request.LoginRequest;
import com.springinfra.dto.request.RegisterRequest;
import com.springinfra.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refresh(String refreshToken);
    void logout(String token);
    void deleteUser(UUID id);
    UserResponse getMe();
    Page<UserResponse> getAllUsers(Pageable pageable);
}
