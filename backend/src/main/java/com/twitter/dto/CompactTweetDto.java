package com.twitter.dto;

import java.util.List;

public class CompactTweetDto {
    private final String id;
    private final AuthorDto author;
    private final String text;
    private final List<MediaDto> media;

    public CompactTweetDto(String id, AuthorDto author, String text, List<MediaDto> media) {
        this.id = id;
        this.author = author;
        this.text = text;
        this.media = media;
    }

    public String getId() { return id; }
    public AuthorDto getAuthor() { return author; }
    public String getText() { return text; }
    public List<MediaDto> getMedia() { return media; }
}
