package com.twitter.dto;

public class TrendDto {
    private final String category;
    private final String topic;

    public TrendDto(String category, String topic) {
        this.category = category;
        this.topic = topic;
    }

    public String getCategory() { return category; }
    public String getTopic() { return topic; }
}
