package com.twitter.dto;

import java.util.List;

public class TweetPageDto {
    private final List<TweetDto> items;
    private final String nextCursor;
    private final boolean hasMore;

    public TweetPageDto(List<TweetDto> items, String nextCursor, boolean hasMore) {
        this.items = items;
        this.nextCursor = nextCursor;
        this.hasMore = hasMore;
    }

    public List<TweetDto> getItems() { return items; }
    public String getNextCursor() { return nextCursor; }
    public boolean isHasMore() { return hasMore; }
}
