package com.bedrockai.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class JwtService {


    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private Duration accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private Duration refreshTokenExpiration;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        try {
            byte[] keyBytes = decodeSecretKeyBytes();
            this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        }

    }

    private byte[] decodeSecretKeyBytes() {
        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException e) {
            bytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        if (bytes.length < 32) {
            bytes = secret.getBytes(StandardCharsets.UTF_8);
            if (bytes.length < 32) {
                throw new IllegalArgumentException("jwt.secret must be at least 32 bytes");
            }
        }
        return bytes;
    }

     public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateToken(String email, Duration expiration, String type) {
        return Jwts.builder()
                .subject(email)
                .claim("type", type)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration.toMillis()))
                .signWith(secretKey)
                .compact();
    }

    public String tokenType(String token) {
        return extractClaims(token).get("type", String.class);
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token).getPayload();
    }

    public String generateAccessToken(UUID id) {
        return generateToken(id.toString(), accessTokenExpiration, "access");
    }

    public String generateRefreshToken(UUID id) {
        return generateToken(id.toString(), refreshTokenExpiration, "refresh");
    }

    public String extractSubject(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}