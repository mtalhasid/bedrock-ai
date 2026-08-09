package com.bedrockai.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {
}