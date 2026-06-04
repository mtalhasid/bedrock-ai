package com.springinfra.service.auth;

import com.springinfra.dto.response.AuthResponse;
import com.springinfra.dto.request.LoginRequest;
import com.springinfra.dto.request.RegisterRequest;
import com.springinfra.dto.response.UserResponse;
import com.springinfra.entity.User;
import com.springinfra.exception.AppException;
import com.springinfra.repository.UserRepository;
import com.springinfra.security.JwtService;
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
                user.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new AppException("Email already exists", HttpStatus.CONFLICT);
        }
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        User saved = userRepository.save(user);
        String accessToken = jwtService.generateAccessToken(saved.getId());
        String refreshToken = jwtService.generateRefreshToken(saved.getId());
        user.setRefreshToken(refreshToken);
        userRepository.save(saved);
        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email()).orElseThrow(
                () -> new AppException("Email not found", HttpStatus.NOT_FOUND)
        );
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AppException("Wrong password", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        User saved = userRepository.save(user);
        String accessToken = jwtService.generateAccessToken(saved.getId());
        String refreshToken = jwtService.generateRefreshToken(saved.getId());
        user.setRefreshToken(refreshToken);
        userRepository.save(saved);
        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public AuthResponse refresh(String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken).orElseThrow(
                () -> new AppException("Invalid token", HttpStatus.NOT_FOUND)
        );
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new AppException("Token not found", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String accessToken = jwtService.generateAccessToken(user.getId());
        String newRefreshToken = jwtService.generateRefreshToken(user.getId());
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);
        return new AuthResponse(accessToken, newRefreshToken);
    }

    @Override
    public void logout(String token) {
        User user = userRepository.findByRefreshToken(token).orElseThrow(
                () -> new AppException("Invalid token", HttpStatus.NOT_FOUND)
        );
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException("not found", HttpStatus.NOT_FOUND);
        }
        UUID userId = (UUID) authentication.getPrincipal();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        return mapToResponse(user);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToResponse);
    }
}