package com.twitter.dto;

public class FollowStateDto {
    private final String targetId;
    private final boolean following;
    private final long followerCount;
    private final boolean followsYou;

    public FollowStateDto(String targetId, boolean following, long followerCount,
                          boolean followsYou) {
        this.targetId = targetId;
        this.following = following;
        this.followerCount = followerCount;
        this.followsYou = followsYou;
    }

    public String getTargetId() { return targetId; }
    public boolean isFollowing() { return following; }
    public long getFollowerCount() { return followerCount; }
    public boolean isFollowsYou() { return followsYou; }
}
