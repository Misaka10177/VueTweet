package com.twitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitter.entity.UserAuth;
import com.twitter.repo.UserAuthRepo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    private final UserAuthRepo userAuthRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TokenInterceptor(UserAuthRepo userAuthRepo) {
        this.userAuthRepo = userAuthRepo;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();

        if ("/login".equals(path) || "/checkUser".equals(path)) {
            return true;
        }

        if ("/refresh".equals(path)) {
            return validateRefreshToken(request, response);
        }

        return validateAccessToken(request, response);
    }

    private boolean validateAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeError(response, "未提供accessToken");
            return false;
        }

        String accessToken = authHeader.substring(7);
        UserAuth userAuth = userAuthRepo.findByAccessToken(accessToken);
        if (userAuth == null) {
            writeError(response, "accessToken无效");
            return false;
        }

        if (LocalDateTime.now().isAfter(userAuth.getTokenCreatedAt().plusDays(1))) {
            writeError(response, "accessToken已过期，请刷新");
            return false;
        }

        request.setAttribute("userId", userAuth.getUserId());
        return true;
    }

    private boolean validateRefreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            writeError(response, "未提供refreshToken");
            return false;
        }

        UserAuth userAuth = userAuthRepo.findByRefreshToken(refreshToken);
        if (userAuth == null) {
            writeError(response, "refreshToken无效");
            return false;
        }

        if (LocalDateTime.now().isAfter(userAuth.getTokenCreatedAt().plusDays(3))) {
            writeError(response, "refreshToken已过期，请重新登录");
            return false;
        }

        request.setAttribute("userId", userAuth.getUserId());
        return true;
    }

    private void writeError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, String> result = new HashMap<>();
        result.put("status", "error");
        result.put("message", message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
