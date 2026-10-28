package com.twitter.service;

public class StoredImage {
    private final String path;
    private final Integer width;
    private final Integer height;
    private final String mimeType;
    private final int sizeBytes;

    public StoredImage(String path, Integer width, Integer height, String mimeType, int sizeBytes) {
        this.path = path;
        this.width = width;
        this.height = height;
        this.mimeType = mimeType;
        this.sizeBytes = sizeBytes;
    }

    public String getPath() { return path; }
    public Integer getWidth() { return width; }
    public Integer getHeight() { return height; }
    public String getMimeType() { return mimeType; }
    public int getSizeBytes() { return sizeBytes; }
}
