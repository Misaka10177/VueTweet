package com.twitter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TweetDto {
    private final String id;
    private final AuthorDto author;
    private final String text;
    private final List<String> images;
    private final LocalDateTime publishTime;
    private final InteractionDto interaction;

    private TweetDto replyTo;
    private Boolean myUpvote;
    private Boolean myTranspond;
    private Boolean myBookmark;

    public TweetDto(String id, AuthorDto author, String text, List<String> images,
                    LocalDateTime publishTime, InteractionDto interaction) {
        this.id = id;
        this.author = author;
        this.text = text;
        this.images = images;
        this.publishTime = publishTime;
        this.interaction = interaction;
    }

    public String getId() { return id; }
    public AuthorDto getAuthor() { return author; }
    public String getText() { return text; }
    public List<String> getImages() { return images; }
    public LocalDateTime getPublishTime() { return publishTime; }
    public InteractionDto getInteraction() { return interaction; }
    public TweetDto getReplyTo() { return replyTo; }
    public void setReplyTo(TweetDto replyTo) { this.replyTo = replyTo; }
    public Boolean getMyUpvote() { return myUpvote; }
    public void setMyUpvote(Boolean myUpvote) { this.myUpvote = myUpvote; }
    public Boolean getMyTranspond() { return myTranspond; }
    public void setMyTranspond(Boolean myTranspond) { this.myTranspond = myTranspond; }
    public Boolean getMyBookmark() { return myBookmark; }
    public void setMyBookmark(Boolean myBookmark) { this.myBookmark = myBookmark; }
}
