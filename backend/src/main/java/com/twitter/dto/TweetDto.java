package com.twitter.dto;

import java.time.Instant;
import java.util.List;

public class TweetDto {
    private final String id;
    private final AuthorDto author;
    private final String text;
    private final List<MediaDto> media;
    private final Instant createdAt;
    private final Instant editedAt;
    private final String replyToId;
    private final CompactTweetDto replyTo;
    private final StatsDto stats;
    private final ViewerDto viewer;

    public TweetDto(String id, AuthorDto author, String text, List<MediaDto> media,
                    Instant createdAt, Instant editedAt, String replyToId,
                    CompactTweetDto replyTo, StatsDto stats, ViewerDto viewer) {
        this.id = id;
        this.author = author;
        this.text = text;
        this.media = media;
        this.createdAt = createdAt;
        this.editedAt = editedAt;
        this.replyToId = replyToId;
        this.replyTo = replyTo;
        this.stats = stats;
        this.viewer = viewer;
    }

    public String getId() { return id; }
    public AuthorDto getAuthor() { return author; }
    public String getText() { return text; }
    public List<MediaDto> getMedia() { return media; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getEditedAt() { return editedAt; }
    public String getReplyToId() { return replyToId; }
    public CompactTweetDto getReplyTo() { return replyTo; }
    public StatsDto getStats() { return stats; }
    public ViewerDto getViewer() { return viewer; }
}
