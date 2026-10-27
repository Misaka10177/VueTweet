package com.twitter.dto;

public class ReplyResult {
    private final String status;
    private final String id;

    public ReplyResult(String status, String id) {
        this.status = status;
        this.id = id;
    }

    public String getStatus() { return status; }
    public String getId() { return id; }
}
