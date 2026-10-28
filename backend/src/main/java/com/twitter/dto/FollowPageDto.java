package com.twitter.dto;

import java.util.List;

public class FollowPageDto {
    private final List<FollowUserDto> items;
    private final String nextCursor;
    private final boolean hasMore;

    public FollowPageDto(List<FollowUserDto> items, String nextCursor, boolean hasMore) {
        this.items = items;
        this.nextCursor = nextCursor;
        this.hasMore = hasMore;
    }

    public List<FollowUserDto> getItems() { return items; }
    public String getNextCursor() { return nextCursor; }
    public boolean isHasMore() { return hasMore; }
}
