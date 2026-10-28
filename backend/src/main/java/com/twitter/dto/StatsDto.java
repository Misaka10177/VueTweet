package com.twitter.dto;

public class StatsDto {
    private final long replies;
    private final long reposts;
    private final long likes;
    private final long bookmarks;
    private final long views;

    public StatsDto(long replies, long reposts, long likes, long bookmarks, long views) {
        this.replies = replies;
        this.reposts = reposts;
        this.likes = likes;
        this.bookmarks = bookmarks;
        this.views = views;
    }

    public long getReplies() { return replies; }
    public long getReposts() { return reposts; }
    public long getLikes() { return likes; }
    public long getBookmarks() { return bookmarks; }
    public long getViews() { return views; }
}
