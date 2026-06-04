package com.springinfra.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {


    @Value("${jwt.secret}")
    private String secret;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = decodeSecretKeyBytes();
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    private byte[] decodeSecretKeyBytes() {
        try {
            byte[] decoded = Base64.getDecoder().decode(secret);
            if (decoded.length >= 32) {
                return decoded;
            }
        } catch (IllegalArgumentException ignored) {
            // fall through to UTF-8 bytes
        }
        byte[] utf8Bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (utf8Bytes.length < 32) {
            throw new IllegalArgumentException("jwt.secret must be at least 32 bytes (UTF-8) or Base64-decoded to 32+ bytes");
        }
        return utf8Bytes;
    }

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public String generateToken(String email, long expiration) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token).getPayload();
    }

    public String generateAccessToken(UUID id) {
        return generateToken(id.toString(), accessTokenExpiration);
    }

    public String generateRefreshToken(UUID id) {
        return generateToken(id.toString(), refreshTokenExpiration);
    }

    public String extractSubject(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            return extractClaims(token).getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

}
