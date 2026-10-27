package com.twitter.service;

import com.twitter.dto.AuthUserDto;

import java.time.LocalDateTime;

/**
 * Internal result of a successful login/refresh. The refresh token is only
 * handed to the controller so it can be written into an HttpOnly cookie.
 */
public class AuthResult {
    private final String accessToken;
    private final long accessExpiresIn;
    private final String refreshToken;
    private final LocalDateTime refreshExpiresAt;
    private final AuthUserDto user;

    public AuthResult(String accessToken, long accessExpiresIn, String refreshToken,
                      LocalDateTime refreshExpiresAt, AuthUserDto user) {
        this.accessToken = accessToken;
        this.accessExpiresIn = accessExpiresIn;
        this.refreshToken = refreshToken;
        this.refreshExpiresAt = refreshExpiresAt;
        this.user = user;
    }

    public String getAccessToken() { return accessToken; }
    public long getAccessExpiresIn() { return accessExpiresIn; }
    public String getRefreshToken() { return refreshToken; }
    public LocalDateTime getRefreshExpiresAt() { return refreshExpiresAt; }
    public AuthUserDto getUser() { return user; }
}
