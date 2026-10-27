package com.twitter.dto;

public class ToggleResult {
    private final String status;
    private final boolean active;
    private final int count;

    public ToggleResult(String status, boolean active, int count) {
        this.status = status;
        this.active = active;
        this.count = count;
    }

    public String getStatus() { return status; }
    public boolean isActive() { return active; }
    public int getCount() { return count; }
}
