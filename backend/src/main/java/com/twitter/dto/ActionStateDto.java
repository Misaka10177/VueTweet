package com.twitter.dto;

public class ActionStateDto {
    private final String tweetId;
    private final StatsDto stats;
    private final ViewerDto viewer;

    public ActionStateDto(String tweetId, StatsDto stats, ViewerDto viewer) {
        this.tweetId = tweetId;
        this.stats = stats;
        this.viewer = viewer;
    }

    public String getTweetId() { return tweetId; }
    public StatsDto getStats() { return stats; }
    public ViewerDto getViewer() { return viewer; }
}
