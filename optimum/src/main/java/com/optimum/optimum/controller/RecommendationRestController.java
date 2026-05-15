package com.optimum.optimum.controller;

import com.optimum.optimum.service.CatalogService;
import com.optimum.optimum.service.PlaybackService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationRestController {
    private final CatalogService catalogService;
    private final PlaybackService playbackService;

    public RecommendationRestController(CatalogService catalogService, PlaybackService playbackService) {
        this.catalogService = catalogService;
        this.playbackService = playbackService;
    }

    @GetMapping("/trending")
    Map<String, Object> trending() {
        return Map.of("period", "THIS_WEEK", "items", catalogService.trending());
    }

    @GetMapping("/continue-watching")
    Map<String, Object> continueWatching() {
        return Map.of("items", playbackService.continueWatching());
    }

    @GetMapping("/personalized")
    Map<String, Object> personalized() {
        return Map.of("rows", List.of(
                Map.of("rowId", "trending", "title", "Tendances", "reason", "TRENDING", "items", catalogService.trending()),
                Map.of("rowId", "originals", "title", "Optimum Originals", "reason", "PRIME_ORIGINALS", "items", catalogService.originals()),
                Map.of("rowId", "new", "title", "Nouveautes", "reason", "NEW_ARRIVALS", "items", catalogService.newReleases())
        ));
    }
}
