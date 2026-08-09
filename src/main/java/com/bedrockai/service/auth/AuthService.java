package com.bedrockai.service.auth;

import com.bedrockai.dto.response.AuthResponse;
import com.bedrockai.dto.request.LoginRequest;
import com.bedrockai.dto.request.RegisterRequest;
import com.bedrockai.dto.response.UserResponse;
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
