package com.twitter.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class JwtService {
    private static final String CLAIM_SESSION_ID = "sid";
    private static final String CLAIM_TYPE = "typ";
    private static final String CLAIM_ROLES = "roles";
    private static final String TYPE_ACCESS = "access";

    private final Key key;
    private final AuthSettings settings;

    public JwtService(AuthSettings settings) {
        this.settings = settings;
        byte[] secret = settings.getJwtSecret().getBytes(StandardCharsets.UTF_8);
        if (secret.length < 32) {
            throw new IllegalStateException("app.jwt.secret must be at least 32 bytes");
        }
        this.key = Keys.hmacShaKeyFor(secret);
    }

    public String issueAccessToken(String userId, String sessionId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setIssuer(settings.getJwtIssuer())
                .setAudience(settings.getJwtAudience())
                .setSubject(userId)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(settings.getAccessTtl())))
                .claim(CLAIM_SESSION_ID, sessionId)
                .claim(CLAIM_TYPE, TYPE_ACCESS)
                .claim(CLAIM_ROLES, Collections.singletonList("USER"))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public AuthPrincipal parseAccessToken(String token) {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(key)
                .requireIssuer(settings.getJwtIssuer())
                .requireAudience(settings.getJwtAudience())
                .setAllowedClockSkewSeconds(30)
                .build()
                .parseClaimsJws(token);
        Claims claims = jws.getBody();
        if (!TYPE_ACCESS.equals(claims.get(CLAIM_TYPE, String.class))) {
            throw new JwtException("Not an access token");
        }
        String userId = claims.getSubject();
        String sessionId = claims.get(CLAIM_SESSION_ID, String.class);
        if (userId == null || sessionId == null) {
            throw new JwtException("Missing token claims");
        }
        List<String> roles = claims.get(CLAIM_ROLES, List.class);
        return new AuthPrincipal(userId, sessionId, roles);
    }

    public long getAccessTtlSeconds() {
        return settings.getAccessTtlSeconds();
    }
}
