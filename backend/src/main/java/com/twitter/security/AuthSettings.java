package com.twitter.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class AuthSettings {
    private final String jwtSecret;
    private final String jwtIssuer;
    private final String jwtAudience;
    private final Duration accessTtl;
    private final Duration refreshTtl;
    private final Duration sessionTtl;
    private final String cookieName;
    private final String cookiePath;
    private final boolean cookieSecure;
    private final String cookieSameSite;
    private final int maxSessions;
    private final List<String> allowedOrigins;

    public AuthSettings(
            @Value("${app.jwt.secret}") String jwtSecret,
            @Value("${app.jwt.issuer:twitter-api}") String jwtIssuer,
            @Value("${app.jwt.audience:twitter-web}") String jwtAudience,
            @Value("${app.jwt.access-ttl:PT15M}") String accessTtl,
            @Value("${app.auth.refresh-ttl:P30D}") String refreshTtl,
            @Value("${app.auth.session-ttl:P30D}") String sessionTtl,
            @Value("${app.auth.cookie-name:refresh_token}") String cookieName,
            @Value("${app.auth.cookie-path:/auth}") String cookiePath,
            @Value("${app.auth.cookie-secure:false}") boolean cookieSecure,
            @Value("${app.auth.cookie-same-site:Lax}") String cookieSameSite,
            @Value("${app.auth.max-sessions:10}") int maxSessions,
            @Value("${app.auth.allowed-origins:http://localhost:5173}") String allowedOrigins) {
        this.jwtSecret = jwtSecret;
        this.jwtIssuer = jwtIssuer;
        this.jwtAudience = jwtAudience;
        this.accessTtl = Duration.parse(accessTtl);
        this.refreshTtl = Duration.parse(refreshTtl);
        this.sessionTtl = Duration.parse(sessionTtl);
        this.cookieName = cookieName;
        this.cookiePath = cookiePath;
        this.cookieSecure = cookieSecure;
        this.cookieSameSite = cookieSameSite;
        this.maxSessions = maxSessions;
        this.allowedOrigins = new ArrayList<>();
        for (String origin : Arrays.asList(allowedOrigins.split(","))) {
            String trimmed = origin.trim();
            if (!trimmed.isEmpty()) {
                this.allowedOrigins.add(trimmed);
            }
        }
    }

    public String getJwtSecret() { return jwtSecret; }
    public String getJwtIssuer() { return jwtIssuer; }
    public String getJwtAudience() { return jwtAudience; }
    public Duration getAccessTtl() { return accessTtl; }
    public long getAccessTtlSeconds() { return accessTtl.getSeconds(); }
    public Duration getRefreshTtl() { return refreshTtl; }
    public Duration getSessionTtl() { return sessionTtl; }
    public String getCookieName() { return cookieName; }
    public String getCookiePath() { return cookiePath; }
    public boolean isCookieSecure() { return cookieSecure; }
    public String getCookieSameSite() { return cookieSameSite; }
    public int getMaxSessions() { return maxSessions; }
    public List<String> getAllowedOrigins() { return allowedOrigins; }
}
