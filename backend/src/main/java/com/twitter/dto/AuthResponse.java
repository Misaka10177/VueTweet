package com.twitter.dto;

public class AuthResponse {
    private final String accessToken;
    private final String tokenType;
    private final long expiresIn;
    private final AuthUserDto user;

    public AuthResponse(String accessToken, long expiresIn, AuthUserDto user) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public String getAccessToken() { return accessToken; }
    public String getTokenType() { return tokenType; }
    public long getExpiresIn() { return expiresIn; }
    public AuthUserDto getUser() { return user; }
}
