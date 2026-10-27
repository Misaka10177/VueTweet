package com.twitter.dto;

import java.time.LocalDateTime;

public class SessionDto {
    private final String id;
    private final String deviceName;
    private final String userAgent;
    private final String ipAddress;
    private final LocalDateTime createdAt;
    private final LocalDateTime lastUsedAt;
    private final boolean current;

    public SessionDto(String id, String deviceName, String userAgent, String ipAddress,
                      LocalDateTime createdAt, LocalDateTime lastUsedAt, boolean current) {
        this.id = id;
        this.deviceName = deviceName;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
        this.createdAt = createdAt;
        this.lastUsedAt = lastUsedAt;
        this.current = current;
    }

    public String getId() { return id; }
    public String getDeviceName() { return deviceName; }
    public String getUserAgent() { return userAgent; }
    public String getIpAddress() { return ipAddress; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastUsedAt() { return lastUsedAt; }
    public boolean isCurrent() { return current; }
}
