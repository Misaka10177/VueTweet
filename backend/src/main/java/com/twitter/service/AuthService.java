package com.twitter.service;

import com.twitter.dto.AuthUserDto;
import com.twitter.dto.SessionDto;
import com.twitter.entity.AuthRefreshToken;
import com.twitter.entity.AuthSession;
import com.twitter.entity.User;
import com.twitter.entity.UserCredential;
import com.twitter.exception.ApiException;
import com.twitter.repo.AuthRefreshTokenRepo;
import com.twitter.repo.AuthSessionRepo;
import com.twitter.repo.UserCredentialRepo;
import com.twitter.repo.UserRepo;
import com.twitter.security.AuthSettings;
import com.twitter.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 15;
    private static final int REUSE_GRACE_SECONDS = 5;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_BYTES = 72;

    private final UserRepo userRepo;
    private final UserCredentialRepo credentialRepo;
    private final AuthSessionRepo sessionRepo;
    private final AuthRefreshTokenRepo refreshTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthAuditService auditService;
    private final AuthSettings settings;

    public AuthService(UserRepo userRepo,
                       UserCredentialRepo credentialRepo,
                       AuthSessionRepo sessionRepo,
                       AuthRefreshTokenRepo refreshTokenRepo,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService,
                       AuthAuditService auditService,
                       AuthSettings settings) {
        this.userRepo = userRepo;
        this.credentialRepo = credentialRepo;
        this.sessionRepo = sessionRepo;
        this.refreshTokenRepo = refreshTokenRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.auditService = auditService;
        this.settings = settings;
    }

    @Transactional(noRollbackFor = ApiException.class)
    public AuthResult login(String username, String password, String ipAddress, String userAgent) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_CREDENTIALS", "用户名或密码不能为空");
        }

        String userId = username.trim();
        LocalDateTime now = LocalDateTime.now();
        UserCredential credential = credentialRepo.findById(userId).orElse(null);
        if (credential == null) {
            auditService.record(null, "LOGIN_FAILED", ipAddress, userAgent, "unknown user: " + userId);
            throw invalidCredentials();
        }

        if (credential.getLockedUntil() != null && credential.getLockedUntil().isAfter(now)) {
            auditService.record(userId, "LOGIN_LOCKED", ipAddress, userAgent, "account is locked");
            throw new ApiException(HttpStatus.LOCKED, "ACCOUNT_LOCKED", "尝试次数过多，请 15 分钟后再试");
        }

        if (!passwordEncoder.matches(password, credential.getPasswordHash())) {
            int attempts = credential.getFailedAttempts() + 1;
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                credential.setFailedAttempts(0);
                credential.setLockedUntil(now.plusMinutes(LOCK_MINUTES));
            } else {
                credential.setFailedAttempts(attempts);
            }
            credentialRepo.save(credential);
            auditService.record(userId, "LOGIN_FAILED", ipAddress, userAgent,
                    "bad password, attempts=" + attempts);
            throw invalidCredentials();
        }

        credential.setFailedAttempts(0);
        credential.setLockedUntil(null);
        credential.setLastLoginAt(now);
        credentialRepo.save(credential);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "用户不存在"));

        enforceMaxSessions(userId, now);
        AuthSession session = createSession(userId, ipAddress, userAgent, now);
        String rawRefreshToken = refreshTokenService.generateRawToken();
        refreshTokenService.create(session.getId(), rawRefreshToken, session.getExpiresAt());
        String accessToken = jwtService.issueAccessToken(userId, session.getId());

        auditService.record(userId, "LOGIN_SUCCESS", ipAddress, userAgent, "session=" + session.getId());
        return new AuthResult(accessToken, jwtService.getAccessTtlSeconds(), rawRefreshToken,
                session.getExpiresAt(), toDto(user));
    }

    @Transactional(noRollbackFor = ApiException.class)
    public AuthResult refresh(String rawRefreshToken, String ipAddress, String userAgent) {
        if (rawRefreshToken == null || rawRefreshToken.isEmpty()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", "请重新登录");
        }

        String tokenHash = refreshTokenService.hash(rawRefreshToken);
        AuthRefreshToken storedToken = refreshTokenRepo.findByTokenHashForUpdate(tokenHash)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", "请重新登录"));

        LocalDateTime now = LocalDateTime.now();
        if (storedToken.getRevokedAt() != null || !storedToken.getExpiresAt().isAfter(now)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", "请重新登录");
        }

        AuthSession session = sessionRepo.findByIdForUpdate(storedToken.getSessionId())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", "请重新登录"));

        if (session.getRevokedAt() != null || !session.getExpiresAt().isAfter(now)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", "登录已过期，请重新登录");
        }

        if (storedToken.getUsedAt() != null) {
            boolean withinGrace = storedToken.getReplacedById() != null
                    && !now.isAfter(storedToken.getUsedAt().plusSeconds(REUSE_GRACE_SECONDS));
            if (withinGrace) {
                // Two tabs refreshed at the same time: the cookie has already been
                // rotated by the winning request, so ask the client to retry.
                throw new ApiException(HttpStatus.UNAUTHORIZED, "REFRESH_RETRY", "刷新冲突，请重试");
            }

            revoke(session, "REUSE_DETECTED");
            auditService.record(session.getUserId(), "REFRESH_REUSE", ipAddress, userAgent,
                    "session=" + session.getId());
            throw new ApiException(HttpStatus.UNAUTHORIZED, "SESSION_REVOKED", "会话已失效，请重新登录");
        }

        storedToken.setUsedAt(now);
        refreshTokenRepo.save(storedToken);

        String newRawToken = refreshTokenService.generateRawToken();
        AuthRefreshToken newToken = refreshTokenService.create(session.getId(), newRawToken, session.getExpiresAt());
        storedToken.setReplacedById(newToken.getId());
        refreshTokenRepo.save(storedToken);

        session.setLastUsedAt(now);
        sessionRepo.save(session);

        String accessToken = jwtService.issueAccessToken(session.getUserId(), session.getId());
        User user = userRepo.findById(session.getUserId())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "用户不存在"));

        return new AuthResult(accessToken, jwtService.getAccessTtlSeconds(), newRawToken,
                session.getExpiresAt(), toDto(user));
    }

    @Transactional
    public void logout(String sessionId, String rawRefreshToken, String ipAddress, String userAgent) {
        AuthSession session = null;
        if (sessionId != null) {
            session = sessionRepo.findById(sessionId).orElse(null);
        } else if (rawRefreshToken != null && !rawRefreshToken.isEmpty()) {
            session = refreshTokenRepo.findByTokenHash(refreshTokenService.hash(rawRefreshToken))
                    .flatMap(token -> sessionRepo.findById(token.getSessionId()))
                    .orElse(null);
        }

        if (session == null || session.getRevokedAt() != null) {
            return;
        }

        revoke(session, "LOGOUT");
        auditService.record(session.getUserId(), "LOGOUT", ipAddress, userAgent,
                "session=" + session.getId());
    }

    @Transactional(noRollbackFor = ApiException.class)
    public void changePassword(String userId, String currentSessionId,
                               String currentPassword, String newPassword,
                               String ipAddress, String userAgent) {
        if (currentPassword == null || currentPassword.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD", "请输入当前密码");
        }
        if (newPassword == null || newPassword.length() < MIN_PASSWORD_LENGTH
                || newPassword.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "WEAK_PASSWORD",
                    "新密码长度至少 " + MIN_PASSWORD_LENGTH + " 位，且不能超过 72 字节");
        }

        UserCredential credential = credentialRepo.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "用户不存在"));

        if (!passwordEncoder.matches(currentPassword, credential.getPasswordHash())) {
            auditService.record(userId, "PASSWORD_CHANGE_FAILED", ipAddress, userAgent, "wrong current password");
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD", "当前密码不正确");
        }
        if (passwordEncoder.matches(newPassword, credential.getPasswordHash())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "SAME_PASSWORD", "新密码不能与当前密码相同");
        }

        credential.setPasswordHash(passwordEncoder.encode(newPassword));
        credential.setPasswordUpdatedAt(LocalDateTime.now());
        credentialRepo.save(credential);

        LocalDateTime now = LocalDateTime.now();
        for (AuthSession session : activeSessions(userId, now)) {
            if (!session.getId().equals(currentSessionId)) {
                revoke(session, "PASSWORD_CHANGED");
            }
        }
        auditService.record(userId, "PASSWORD_CHANGED", ipAddress, userAgent, "other sessions revoked");
    }

    @Transactional(readOnly = true)
    public List<SessionDto> listSessions(String userId, String currentSessionId) {
        List<SessionDto> result = new ArrayList<>();
        for (AuthSession session : activeSessions(userId, LocalDateTime.now())) {
            result.add(new SessionDto(session.getId(), session.getDeviceName(), session.getUserAgent(),
                    session.getIpAddress(), session.getCreatedAt(), session.getLastUsedAt(),
                    session.getId().equals(currentSessionId)));
        }
        return result;
    }

    @Transactional
    public void revokeSession(String userId, String sessionId, String ipAddress, String userAgent) {
        AuthSession session = sessionRepo.findById(sessionId)
                .filter(value -> value.getUserId().equals(userId))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "SESSION_NOT_FOUND", "会话不存在"));
        if (session.getRevokedAt() == null) {
            revoke(session, "REVOKED_BY_USER");
            auditService.record(userId, "SESSION_REVOKED", ipAddress, userAgent, "session=" + sessionId);
        }
    }

    private List<AuthSession> activeSessions(String userId, LocalDateTime now) {
        return sessionRepo.findByUserIdAndRevokedAtIsNullAndExpiresAtAfterOrderByLastUsedAtDesc(userId, now);
    }

    private void enforceMaxSessions(String userId, LocalDateTime now) {
        int max = settings.getMaxSessions();
        if (max <= 0) {
            return;
        }
        List<AuthSession> active = sessionRepo
                .findByUserIdAndRevokedAtIsNullAndExpiresAtAfterOrderByLastUsedAtAsc(userId, now);
        int excess = active.size() - max + 1;
        for (int i = 0; i < excess && i < active.size(); i++) {
            revoke(active.get(i), "SESSION_LIMIT");
        }
    }

    private AuthSession createSession(String userId, String ipAddress, String userAgent, LocalDateTime now) {
        AuthSession session = new AuthSession();
        session.setId(UUID.randomUUID().toString());
        session.setUserId(userId);
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        session.setCreatedAt(now);
        session.setLastUsedAt(now);
        session.setExpiresAt(now.plus(settings.getSessionTtl()));
        return sessionRepo.save(session);
    }

    private void revoke(AuthSession session, String reason) {
        session.setRevokedAt(LocalDateTime.now());
        session.setRevokeReason(reason);
        sessionRepo.save(session);
    }

    private AuthUserDto toDto(User user) {
        return new AuthUserDto(user.getId(), user.getName(), user.getProfilePhoto());
    }

    private ApiException invalidCredentials() {
        return new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "用户名或密码错误");
    }
}
