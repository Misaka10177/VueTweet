package com.twitter.service;

import com.twitter.entity.AuthRefreshToken;
import com.twitter.repo.AuthRefreshTokenRepo;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class RefreshTokenService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 32;

    private final AuthRefreshTokenRepo refreshTokenRepo;

    public RefreshTokenService(AuthRefreshTokenRepo refreshTokenRepo) {
        this.refreshTokenRepo = refreshTokenRepo;
    }

    public String generateRawToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(Character.forDigit((b >> 4) & 0xF, 16));
                hex.append(Character.forDigit(b & 0xF, 16));
            }
            return hex.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot hash refresh token", ex);
        }
    }

    public AuthRefreshToken create(String sessionId, String rawToken, LocalDateTime expiresAt) {
        LocalDateTime now = LocalDateTime.now();
        AuthRefreshToken token = new AuthRefreshToken();
        token.setSessionId(sessionId);
        token.setTokenHash(hash(rawToken));
        token.setIssuedAt(now);
        token.setExpiresAt(expiresAt);
        return refreshTokenRepo.save(token);
    }
}
