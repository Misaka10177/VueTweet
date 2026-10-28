package com.twitter.model;

public enum InteractionType {
    LIKE(1),
    REPOST(2),
    BOOKMARK(3);

    private final int code;

    InteractionType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static InteractionType fromCode(int code) {
        for (InteractionType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
