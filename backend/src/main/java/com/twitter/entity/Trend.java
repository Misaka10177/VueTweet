package com.twitter.entity;

import javax.persistence.*;

@Entity
@Table(name = "trend")
public class Trend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;
    private String topic;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
}
