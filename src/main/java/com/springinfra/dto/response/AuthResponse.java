package com.springinfra.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {
}