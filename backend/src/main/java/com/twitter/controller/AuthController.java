package com.twitter.controller;

import com.twitter.entity.UserAuth;
import com.twitter.repo.UserAuthRepo;
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

    private final UserAuthRepo userAuthRepo;

    public AuthController(UserAuthRepo userAuthRepo) {
        this.userAuthRepo = userAuthRepo;
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

        UserAuth userAuth = userAuthRepo.findById(username.trim()).orElse(null);
        if (userAuth == null || !hashPassword(password).equals(userAuth.getPassword())) {
            result.put("status", "error");
            result.put("message", "用户名或密码错误");
            return result;
        }

        String accessToken = UUID.randomUUID().toString().replace("-", "");
        String refreshToken = UUID.randomUUID().toString().replace("-", "");

        userAuth.setAccessToken(accessToken);
        userAuth.setRefreshToken(refreshToken);
        userAuth.setTokenCreatedAt(LocalDateTime.now());
        userAuthRepo.save(userAuth);

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
        UserAuth userAuth = userAuthRepo.findById(userId).orElse(null);

        String newAccessToken = UUID.randomUUID().toString().replace("-", "");
        String newRefreshToken = UUID.randomUUID().toString().replace("-", "");

        userAuth.setAccessToken(newAccessToken);
        userAuth.setRefreshToken(newRefreshToken);
        userAuth.setTokenCreatedAt(LocalDateTime.now());
        userAuthRepo.save(userAuth);

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

        UserAuth userAuth = userAuthRepo.findById(userId).orElse(null);
        if (userAuth != null) {
            userAuth.setAccessToken(null);
            userAuth.setRefreshToken(null);
            userAuth.setTokenCreatedAt(null);
            userAuthRepo.save(userAuth);
        }

        String cookieValue = "refreshToken=; Path=/; HttpOnly; Max-Age=0; SameSite=None; Secure";
        response.addHeader("Set-Cookie", cookieValue);

        result.put("status", "success");
        result.put("message", "登出成功");
        return result;
    }

    private void setRefreshTokenCookie(String refreshToken, HttpServletResponse response) {
        String cookieValue = String.format(
            "refreshToken=%s; Path=/; HttpOnly; Max-Age=%d; SameSite=None; Secure",
            refreshToken, 3 * 24 * 60 * 60
        );
        response.addHeader("Set-Cookie", cookieValue);
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
