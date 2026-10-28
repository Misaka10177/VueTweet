package com.twitter.dto;

public class FollowUserDto {
    private final String id;
    private final String name;
    private final String avatarUrl;
    private final boolean following;
    private final boolean followsYou;
    private final boolean self;

    public FollowUserDto(String id, String name, String avatarUrl,
                         boolean following, boolean followsYou, boolean self) {
        this.id = id;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.following = following;
        this.followsYou = followsYou;
        this.self = self;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAvatarUrl() { return avatarUrl; }
    public boolean isFollowing() { return following; }
    public boolean isFollowsYou() { return followsYou; }
    public boolean isSelf() { return self; }
}
