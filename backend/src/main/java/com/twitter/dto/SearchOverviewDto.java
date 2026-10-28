package com.twitter.dto;

import java.util.List;

public class SearchOverviewDto {
    private final String query;
    private final List<FollowUserDto> users;
    private final TweetPageDto tweets;
    private final List<TrendDto> topics;

    public SearchOverviewDto(String query, List<FollowUserDto> users,
                             TweetPageDto tweets, List<TrendDto> topics) {
        this.query = query;
        this.users = users;
        this.tweets = tweets;
        this.topics = topics;
    }

    public String getQuery() { return query; }
    public List<FollowUserDto> getUsers() { return users; }
    public TweetPageDto getTweets() { return tweets; }
    public List<TrendDto> getTopics() { return topics; }
}
