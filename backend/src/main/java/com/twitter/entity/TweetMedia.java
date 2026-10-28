package com.twitter.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tweet_media")
public class TweetMedia {
    @EmbeddedId
    private TweetMediaId id;

    @Column(nullable = false, length = 512)
    private String url;

    private Integer width;
    private Integer height;

    @Column(name = "mime_type", length = 64)
    private String mimeType;

    @Column(name = "size_bytes")
    private Integer sizeBytes;

    @Column(name = "alt_text", length = 420)
    private String altText;

    public TweetMediaId getId() { return id; }
    public void setId(TweetMediaId id) { this.id = id; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }
    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public Integer getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(Integer sizeBytes) { this.sizeBytes = sizeBytes; }
    public String getAltText() { return altText; }
    public void setAltText(String altText) { this.altText = altText; }
}
