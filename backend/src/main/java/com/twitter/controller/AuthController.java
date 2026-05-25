package com.twitter.controller;

import com.twitter.entity.Account;
import com.twitter.entity.Token;
import com.twitter.repo.AccountRepo;
import com.twitter.repo.TokenRepo;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;

@RestController
public class AuthController {

    private final AccountRepo accountRepo;
    private final TokenRepo tokenRepo;

    public AuthController(AccountRepo accountRepo, TokenRepo tokenRepo) {
        this.accountRepo = accountRepo;
        this.tokenRepo = tokenRepo;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> body, HttpServletResponse response) {
        Map<String, String> result = new HashMap<>();
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            result.put("status", "error");
            result.put("message", "用户名或密码不能为空");
            return result;
        }

        Account account = accountRepo.findById(username.trim()).orElse(null);
        if (account == null || !hashPassword(password).equals(account.getPassword())) {
            result.put("status", "error");
            result.put("message", "用户名或密码错误");
            return result;
        }

        String accessToken = UUID.randomUUID().toString().replace("-", "");
        String refreshToken = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime now = LocalDateTime.now();

        Token token = tokenRepo.findByUserId(username.trim());
        if (token == null) {
            token = new Token();
            token.setUserId(username.trim());
        }
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setCreatedAt(now);
        tokenRepo.save(token);

        setRefreshTokenCookie(refreshToken, response);

        result.put("status", "success");
        result.put("message", "登录成功");
        result.put("accessToken", accessToken);
        return result;
    }

    @PostMapping("/refresh")
    public Map<String, String> refresh(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> result = new HashMap<>();
        String userId = (String) request.getAttribute("userId");
        Token matched = tokenRepo.findByUserId(userId);

        String newAccessToken = UUID.randomUUID().toString().replace("-", "");
        String newRefreshToken = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime now = LocalDateTime.now();

        matched.setAccessToken(newAccessToken);
        matched.setRefreshToken(newRefreshToken);
        matched.setCreatedAt(now);
        tokenRepo.save(matched);

        setRefreshTokenCookie(newRefreshToken, response);

        result.put("status", "success");
        result.put("message", "刷新成功");
        result.put("accessToken", newAccessToken);
        return result;
    }

    @PostMapping("/logout")
    public Map<String, String> logout(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> result = new HashMap<>();
        String userId = (String) request.getAttribute("userId");

        Token token = tokenRepo.findByUserId(userId);
        if (token != null) {
            tokenRepo.delete(token);
        }

        String cookieValue = "refreshToken=; Path=/; HttpOnly; Max-Age=0; SameSite=None; Secure";
        response.addHeader("Set-Cookie", cookieValue);

        result.put("status", "success");
        result.put("message", "登出成功");
        return result;
    }

    private void setRefreshTokenCookie(String refreshToken, HttpServletResponse response) {
        // 手动设置Set-Cookie头，确保跨域请求能携带cookie
        String cookieValue = String.format(
            "refreshToken=%s; Path=/; HttpOnly; Max-Age=%d; SameSite=None; Secure",
            refreshToken, 3 * 24 * 60 * 60
        );
        response.addHeader("Set-Cookie", cookieValue);
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
