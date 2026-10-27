package com.twitter.dto;

public class InteractionDto {
    private final int reply;
    private final int transpond;
    private final int upvote;
    private final int view;
    private final int bookmark;

    public InteractionDto(int reply, int transpond, int upvote, int view, int bookmark) {
        this.reply = reply;
        this.transpond = transpond;
        this.upvote = upvote;
        this.view = view;
        this.bookmark = bookmark;
    }

    public int getReply() { return reply; }
    public int getTranspond() { return transpond; }
    public int getUpvote() { return upvote; }
    public int getView() { return view; }
    public int getBookmark() { return bookmark; }
}
