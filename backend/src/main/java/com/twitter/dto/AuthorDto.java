package com.twitter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthorDto {
    private final String id;
    private final String name;

    @JsonProperty("profile_photo")
    private final String profilePhoto;

    public AuthorDto(String id, String name, String profilePhoto) {
        this.id = id;
        this.name = name;
        this.profilePhoto = profilePhoto;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getProfilePhoto() { return profilePhoto; }
}
