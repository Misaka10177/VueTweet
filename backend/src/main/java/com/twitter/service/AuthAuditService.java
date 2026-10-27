package com.twitter.service;

import com.twitter.entity.AuthAuditLog;
import com.twitter.repo.AuthAuditLogRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthAuditService {
    private final AuthAuditLogRepo auditLogRepo;

    public AuthAuditService(AuthAuditLogRepo auditLogRepo) {
        this.auditLogRepo = auditLogRepo;
    }

    public void record(String userId, String eventType, String ipAddress, String userAgent, String detail) {
        AuthAuditLog log = new AuthAuditLog();
        log.setUserId(userId);
        log.setEventType(truncate(eventType, 30));
        log.setIpAddress(truncate(ipAddress, 45));
        log.setUserAgent(truncate(userAgent, 512));
        log.setDetail(truncate(detail, 500));
        log.setCreatedAt(LocalDateTime.now());
        auditLogRepo.save(log);
    }

    private String truncate(String value, int max) {
        if (value == null) {
            return null;
        }
        return value.length() <= max ? value : value.substring(0, max);
    }
}
