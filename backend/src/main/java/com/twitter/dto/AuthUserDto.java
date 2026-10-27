package com.twitter.dto;

public class AuthUserDto {
    private final String id;
    private final String name;
    private final String profilePhoto;

    public AuthUserDto(String id, String name, String profilePhoto) {
        this.id = id;
        this.name = name;
        this.profilePhoto = profilePhoto;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getProfilePhoto() { return profilePhoto; }
}
