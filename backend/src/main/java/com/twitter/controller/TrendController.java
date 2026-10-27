package com.twitter.controller;

import com.twitter.dto.TrendDto;
import com.twitter.service.TrendService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TrendController {
    private final TrendService trendService;

    public TrendController(TrendService trendService) {
        this.trendService = trendService;
    }

    @GetMapping("/trends")
    public List<TrendDto> getTrends() {
        return trendService.list();
    }
}
