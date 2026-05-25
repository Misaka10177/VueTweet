package com.twitter.controller;

import com.twitter.entity.Trend;
import com.twitter.repo.TrendRepo;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class TrendController {

    private final TrendRepo trendRepo;

    public TrendController(TrendRepo trendRepo) {
        this.trendRepo = trendRepo;
    }

    @GetMapping("/trends")
    public List<Map<String, String>> getTrends() {
        List<Trend> trends = trendRepo.findAll();
        List<Map<String, String>> result = new ArrayList<>();
        for (Trend t : trends) {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("category", t.getCategory());
            map.put("topic", t.getTopic());
            result.add(map);
        }
        return result;
    }
}
