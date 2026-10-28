package com.twitter.dto;

public class MediaDto {
    private final String url;
    private final Integer width;
    private final Integer height;
    private final String alt;

    public MediaDto(String url, Integer width, Integer height, String alt) {
        this.url = url;
        this.width = width;
        this.height = height;
        this.alt = alt;
    }

    public String getUrl() { return url; }
    public Integer getWidth() { return width; }
    public Integer getHeight() { return height; }
    public String getAlt() { return alt; }
}
