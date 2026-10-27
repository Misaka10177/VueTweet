package com.twitter.entity;

import javax.persistence.*;

@Entity
@Table(name = "`user`")
public class User {
    @Id
    @Column(length = 50)
    private String id;

    @Column(length = 255)
    private String name;

    @Column(name = "profile_photo", length = 255)
    private String profilePhoto;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }
}
