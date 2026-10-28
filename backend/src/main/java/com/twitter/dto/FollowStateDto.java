package com.twitter.dto;

public class FollowStateDto {
    private final String targetId;
    private final boolean following;
    private final long followerCount;

    public FollowStateDto(String targetId, boolean following, long followerCount) {
        this.targetId = targetId;
        this.following = following;
        this.followerCount = followerCount;
    }

    public String getTargetId() { return targetId; }
    public boolean isFollowing() { return following; }
    public long getFollowerCount() { return followerCount; }
}
