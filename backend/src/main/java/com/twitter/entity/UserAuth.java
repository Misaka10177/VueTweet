package com.twitter.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_auth")
public class UserAuth {
    @Id
    @Column(name = "user_id")
    private String userId;

    private String password;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "token_created_at")
    private LocalDateTime tokenCreatedAt;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public LocalDateTime getTokenCreatedAt() { return tokenCreatedAt; }
    public void setTokenCreatedAt(LocalDateTime tokenCreatedAt) { this.tokenCreatedAt = tokenCreatedAt; }
}
