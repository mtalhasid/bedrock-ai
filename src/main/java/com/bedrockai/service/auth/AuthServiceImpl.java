package com.bedrockai.service.auth;

import com.bedrockai.dto.response.AuthResponse;
import com.bedrockai.dto.request.LoginRequest;
import com.bedrockai.dto.request.RegisterRequest;
import com.bedrockai.dto.response.UserResponse;
import com.bedrockai.entity.User;
import com.bedrockai.exception.AppException;
import com.bedrockai.repository.UserRepository;
import com.bedrockai.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    private User getUserByRefreshToken(String rawToken) {
        return userRepository.findByRefreshToken(jwtService.hashToken(rawToken))
                .orElseThrow(() -> new AppException("Invalid token", HttpStatus.NOT_FOUND));
    }

    private void saveRefreshToken(User user, String rawToken) {
        user.setRefreshToken(jwtService.hashToken(rawToken));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new AppException("Email already exists", HttpStatus.CONFLICT);
        }
        User saved = userRepository.save(User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build());
        String accessToken = jwtService.generateAccessToken(saved.getId());
        String refreshToken = jwtService.generateRefreshToken(saved.getId());
        saveRefreshToken(saved, refreshToken);
        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email()).orElseThrow(
                () -> new AppException("Email not found", HttpStatus.NOT_FOUND));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AppException("Wrong password", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String accessToken = jwtService.generateAccessToken(user.getId());
        String refreshToken = jwtService.generateRefreshToken(user.getId());
        saveRefreshToken(user, refreshToken);
        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public AuthResponse refresh(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new AppException("Token not found", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        User user = getUserByRefreshToken(refreshToken);
        String accessToken = jwtService.generateAccessToken(user.getId());
        String newRefreshToken = jwtService.generateRefreshToken(user.getId());
        saveRefreshToken(user, newRefreshToken);
        return new AuthResponse(accessToken, newRefreshToken);
    }

    @Override
    public void logout(String token) {
        User user = getUserByRefreshToken(token);
        user.setRefreshToken(null);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        userRepository.delete(user);
    }

    @Override
    public UserResponse getMe() {
        UUID userId = getAuthenticatedUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        return mapToResponse(user);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToResponse);
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UUID userId)) {
            throw new AppException("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return userId;
    }
}