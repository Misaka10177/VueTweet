package com.twitter.dto;

public class UserProfileDto {
    private final String id;
    private final String name;
    private final String avatarUrl;
    private final long followingCount;
    private final long followerCount;
    private final long tweetCount;
    private final boolean viewerFollowing;
    private final boolean followsYou;
    private final boolean self;

    public UserProfileDto(String id, String name, String avatarUrl,
                          long followingCount, long followerCount, long tweetCount,
                          boolean viewerFollowing, boolean followsYou, boolean self) {
        this.id = id;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.followingCount = followingCount;
        this.followerCount = followerCount;
        this.tweetCount = tweetCount;
        this.viewerFollowing = viewerFollowing;
        this.followsYou = followsYou;
        this.self = self;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAvatarUrl() { return avatarUrl; }
    public long getFollowingCount() { return followingCount; }
    public long getFollowerCount() { return followerCount; }
    public long getTweetCount() { return tweetCount; }
    public boolean isViewerFollowing() { return viewerFollowing; }
    public boolean isFollowsYou() { return followsYou; }
    public boolean isSelf() { return self; }
}
