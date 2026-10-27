package com.twitter.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitter.dto.ApiError;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

/**
 * CSRF defence for the cookie-authenticated endpoints. SameSite=Lax already
 * blocks most cross-site POSTs; this filter additionally rejects an explicit
 * foreign Origin/Referer. Non-browser clients without either header (curl,
 * server-to-server) are allowed.
 */
@Component
public class OriginValidationFilter extends OncePerRequestFilter {
    private final AuthSettings settings;
    private final ObjectMapper objectMapper;

    public OriginValidationFilter(AuthSettings settings, ObjectMapper objectMapper) {
        this.settings = settings;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!isCookieAuthenticatedEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String origin = request.getHeader("Origin");
        if (origin == null) {
            origin = originOf(request.getHeader("Referer"));
        }

        if (origin != null && !settings.getAllowedOrigins().contains(origin)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(),
                    new ApiError("INVALID_ORIGIN", "请求来源不被允许"));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isCookieAuthenticatedEndpoint(HttpServletRequest request) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        String path = request.getRequestURI();
        return "/auth/refresh".equals(path) || "/auth/logout".equals(path);
    }

    private String originOf(String referer) {
        if (referer == null || referer.isEmpty()) {
            return null;
        }
        try {
            URI uri = URI.create(referer);
            if (uri.getScheme() == null || uri.getHost() == null) {
                return null;
            }
            StringBuilder origin = new StringBuilder(uri.getScheme()).append("://").append(uri.getHost());
            if (uri.getPort() != -1) {
                origin.append(':').append(uri.getPort());
            }
            return origin.toString();
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
