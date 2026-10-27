package com.twitter.controller;

import com.twitter.dto.AuthResponse;
import com.twitter.dto.SessionDto;
import com.twitter.exception.ApiException;
import com.twitter.security.AuthPrincipal;
import com.twitter.security.AuthSettings;
import com.twitter.service.AuthResult;
import com.twitter.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthSettings settings;

    public AuthController(AuthService authService, AuthSettings settings) {
        this.authService = authService;
        this.settings = settings;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody Map<String, String> body,
                              HttpServletRequest request, HttpServletResponse response) {
        AuthResult result = authService.login(body.get("username"), body.get("password"),
                clientIp(request), userAgent(request));
        writeRefreshCookie(response, result);
        return toResponse(result);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        String rawToken = readRefreshCookie(request);
        AuthResult result;
        try {
            result = authService.refresh(rawToken, clientIp(request), userAgent(request));
        } catch (ApiException ex) {
            if (!"REFRESH_RETRY".equals(ex.getCode())) {
                clearRefreshCookie(response);
            }
            throw ex;
        }
        writeRefreshCookie(response, result);
        return toResponse(result);
    }

    @PostMapping("/logout")
    public Map<String, String> logout(Authentication authentication,
                                      HttpServletRequest request, HttpServletResponse response) {
        AuthPrincipal principal = principalOf(authentication);
        String sessionId = principal == null ? null : principal.getSessionId();
        authService.logout(sessionId, readRefreshCookie(request), clientIp(request), userAgent(request));
        clearRefreshCookie(response);

        Map<String, String> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "登出成功");
        return result;
    }

    @GetMapping("/sessions")
    public List<SessionDto> sessions(@AuthenticationPrincipal AuthPrincipal principal) {
        return authService.listSessions(requirePrincipal(principal).getUserId(), principal.getSessionId());
    }

    @DeleteMapping("/sessions/{id}")
    public Map<String, String> revokeSession(@AuthenticationPrincipal AuthPrincipal principal,
                                             @PathVariable String id,
                                             HttpServletRequest request) {
        AuthPrincipal current = requirePrincipal(principal);
        authService.revokeSession(current.getUserId(), id, clientIp(request), userAgent(request));
        Map<String, String> result = new HashMap<>();
        result.put("status", "success");
        return result;
    }

    @PostMapping("/change-password")
    public Map<String, String> changePassword(@AuthenticationPrincipal AuthPrincipal principal,
                                              @RequestBody Map<String, String> body,
                                              HttpServletRequest request) {
        AuthPrincipal current = requirePrincipal(principal);
        authService.changePassword(current.getUserId(), current.getSessionId(),
                body.get("currentPassword"), body.get("newPassword"),
                clientIp(request), userAgent(request));
        Map<String, String> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "密码已修改，其他设备已退出登录");
        return result;
    }

    private AuthResponse toResponse(AuthResult result) {
        return new AuthResponse(result.getAccessToken(), result.getAccessExpiresIn(), result.getUser());
    }

    private AuthPrincipal requirePrincipal(AuthPrincipal principal) {
        if (principal == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "请先登录");
        }
        return principal;
    }

    private AuthPrincipal principalOf(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthPrincipal)) {
            return null;
        }
        return (AuthPrincipal) authentication.getPrincipal();
    }

    private void writeRefreshCookie(HttpServletResponse response, AuthResult result) {
        long maxAge = Duration.between(LocalDateTime.now(), result.getRefreshExpiresAt()).getSeconds();
        ResponseCookie cookie = ResponseCookie.from(settings.getCookieName(), result.getRefreshToken())
                .httpOnly(true)
                .secure(settings.isCookieSecure())
                .path(settings.getCookiePath())
                .maxAge(Duration.ofSeconds(Math.max(0, maxAge)))
                .sameSite(settings.getCookieSameSite())
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(settings.getCookieName(), "")
                .httpOnly(true)
                .secure(settings.isCookieSecure())
                .path(settings.getCookiePath())
                .maxAge(0)
                .sameSite(settings.getCookieSameSite())
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String readRefreshCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (settings.getCookieName().equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String clientIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    private String userAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
