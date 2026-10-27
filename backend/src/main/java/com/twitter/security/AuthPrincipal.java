package com.twitter.security;

import java.util.Collections;
import java.util.List;

public class AuthPrincipal {
    private final String userId;
    private final String sessionId;
    private final List<String> roles;

    public AuthPrincipal(String userId, String sessionId, List<String> roles) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.roles = roles == null ? Collections.<String>emptyList() : roles;
    }

    public String getUserId() { return userId; }
    public String getSessionId() { return sessionId; }
    public List<String> getRoles() { return roles; }
}
