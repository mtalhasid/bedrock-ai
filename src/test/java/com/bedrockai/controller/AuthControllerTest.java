package com.bedrockai.controller;

import com.bedrockai.dto.request.LoginRequest;
import com.bedrockai.dto.request.RefreshTokenRequest;
import com.bedrockai.dto.request.RegisterRequest;
import com.bedrockai.dto.response.ApiResponse;
import com.bedrockai.dto.response.AuthResponse;
import com.bedrockai.dto.response.UserResponse;
import com.bedrockai.service.auth.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void register_returns201_whenSuccessful() {
        // Arrange
        RegisterRequest request = new RegisterRequest("talha", "talha@example.com", "password123");
        AuthResponse mockResponse = mock(AuthResponse.class);
        when(authService.register(request)).thenReturn(mockResponse);

        // Act
        ResponseEntity<ApiResponse<AuthResponse>> response = authController.register(request);

        // Assert
        assertEquals(201, response.getStatusCode().value());
        assertTrue(response.getBody().isSuccess());
        assertEquals(mockResponse, response.getBody().getData());

        verify(authService).register(request);
    }

    @Test
    void login_returns200_whenSuccessful() {
        // Arrange
        LoginRequest request = new LoginRequest("talha@example.com", "password123");
        AuthResponse mockResponse = mock(AuthResponse.class);
        when(authService.login(request)).thenReturn(mockResponse);

        // Act
        ResponseEntity<ApiResponse<AuthResponse>> response = authController.login(request);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isSuccess());
        assertEquals(mockResponse, response.getBody().getData());

        verify(authService).login(request);
    }

    @Test
    void refresh_returns200_whenSuccessful() {
        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest("some-refresh-token");
        AuthResponse mockResponse = mock(AuthResponse.class);
        when(authService.refresh(request.refreshToken())).thenReturn(mockResponse);

        // Act
        ResponseEntity<ApiResponse<AuthResponse>> response = authController.refresh(request);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isSuccess());
        assertEquals(mockResponse, response.getBody().getData());

        verify(authService).refresh(request.refreshToken());
    }

    @Test
    void logout_returns200_whenSuccessful() {
        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest("some-refresh-token");

        // Act
        ResponseEntity<ApiResponse<Void>> response = authController.logout(request);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isSuccess());

        verify(authService).logout(request.refreshToken());
    }

    @Test
    void getMe_returns200_whenSuccessful() {
        // Arrange
        UserResponse mockResponse = mock(UserResponse.class);
        when(authService.getMe()).thenReturn(mockResponse);

        // Act
        ResponseEntity<ApiResponse<UserResponse>> response = authController.getMe();

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isSuccess());
        assertEquals(mockResponse, response.getBody().getData());

        verify(authService).getMe();
    }

    @Test
    void getAllUsers_returns200_whenSuccessful() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<UserResponse> mockPage = Page.empty();
        when(authService.getAllUsers(pageable)).thenReturn(mockPage);

        // Act
        ResponseEntity<ApiResponse<Page<UserResponse>>> response = authController.getAllUsers(pageable);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isSuccess());
        assertEquals(mockPage, response.getBody().getData());

        verify(authService).getAllUsers(pageable);
    }

    @Test
    void deleteUser_returns200_whenSuccessful() {
        // Arrange
        UUID userId = UUID.randomUUID();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userId);

        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Act
            ResponseEntity<ApiResponse<Void>> response = authController.deleteUser();

            // Assert
            assertEquals(200, response.getStatusCode().value());
            assertTrue(response.getBody().isSuccess());

            verify(authService).deleteUser(userId);
        }
    }
}
