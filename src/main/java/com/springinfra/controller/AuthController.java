package com.springinfra.controller;

import com.springinfra.dto.request.LoginRequest;
import com.springinfra.dto.request.RefreshTokenRequest;
import com.springinfra.dto.request.RegisterRequest;
import com.springinfra.dto.response.ApiResponse;
import com.springinfra.dto.response.AuthResponse;
import com.springinfra.dto.response.UserResponse;
import com.springinfra.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse data = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(HttpStatus.CREATED, data));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse data = authService.login(request);
        return ResponseEntity.ok(success(HttpStatus.OK, data));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse data = authService.refresh(request.refreshToken());
        return ResponseEntity.ok(success(HttpStatus.OK, data));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.ok(success(HttpStatus.OK, null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe() {
        UserResponse data = authService.getMe();
        return ResponseEntity.ok(success(HttpStatus.OK, data));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<UserResponse> data = authService.getAllUsers(pageable);
        return ResponseEntity.ok(success(HttpStatus.OK, data));
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID id = (UUID) authentication.getPrincipal();
        authService.deleteUser(id);
        return ResponseEntity.ok(success(HttpStatus.OK, null));
    }

    private static <T> ApiResponse<T> success(HttpStatus status, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(status.value())
                .message("Success")
                .data(data)
                .build();
    }
}
