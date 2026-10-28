package com.twitter.util;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Opaque keyset cursor: created_at + id. The timestamp keeps the database
 * precision (LocalDateTime.toString), so items with the same millisecond are
 * not skipped.
 */
public final class Cursor {
    private final LocalDateTime createdAt;
    private final long id;

    public Cursor(LocalDateTime createdAt, long id) {
        this.createdAt = createdAt;
        this.id = id;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public long getId() { return id; }

    public String encode() {
        String raw = createdAt.toString() + "|" + id;
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static Cursor decode(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        try {
            String raw = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            int split = raw.lastIndexOf('|');
            if (split <= 0) {
                throw new IllegalArgumentException("bad cursor");
            }
            LocalDateTime createdAt = LocalDateTime.parse(raw.substring(0, split));
            long id = Long.parseLong(raw.substring(split + 1));
            return new Cursor(createdAt, id);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("bad cursor", ex);
        }
    }
}
