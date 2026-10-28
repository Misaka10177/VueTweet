package com.twitter.dto;

public class ViewerDto {
    private final boolean liked;
    private final boolean reposted;
    private final boolean bookmarked;

    public ViewerDto(boolean liked, boolean reposted, boolean bookmarked) {
        this.liked = liked;
        this.reposted = reposted;
        this.bookmarked = bookmarked;
    }

    public boolean isLiked() { return liked; }
    public boolean isReposted() { return reposted; }
    public boolean isBookmarked() { return bookmarked; }
}
