package com.optimum.optimum.controller;

import com.optimum.optimum.dto.ApiDtos.PageResponse;
import com.optimum.optimum.model.Episode;
import com.optimum.optimum.model.Genre;
import com.optimum.optimum.model.VideoTitle;
import com.optimum.optimum.service.CatalogService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CatalogRestController {
    private final CatalogService catalogService;

    public CatalogRestController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/titles")
    PageResponse<VideoTitle> titles(@RequestParam(name = "type", required = false) String type,
                                    @RequestParam(name = "genre", required = false) String genre,
                                    @RequestParam(required = false, name = "q") String query) {
        List<VideoTitle> data = catalogService.listTitles(type, genre, query);
        return new PageResponse<>(data, Map.of("hasMore", false, "total", data.size()));
    }

    @GetMapping("/titles/{titleId}")
    VideoTitle title(@PathVariable("titleId") UUID titleId) {
        return catalogService.getTitle(titleId);
    }

    @GetMapping("/titles/{titleId}/seasons")
    Map<String, Object> seasons(@PathVariable("titleId") UUID titleId) {
        VideoTitle title = catalogService.getTitle(titleId);
        int count = title.getSeasonCount() == null ? 0 : title.getSeasonCount();
        return Map.of("titleId", titleId, "seasons", count);
    }

    @GetMapping("/titles/{titleId}/seasons/{seasonNumber}/episodes")
    Map<String, Object> episodes(@PathVariable("titleId") UUID titleId, @PathVariable("seasonNumber") int seasonNumber) {
        return Map.of("titleId", titleId, "seasonNumber", seasonNumber, "episodes", catalogService.listEpisodes(titleId, seasonNumber));
    }

    @GetMapping("/titles/{titleId}/seasons/{seasonNumber}/episodes/{episodeNumber}")
    Episode episode(@PathVariable("titleId") UUID titleId, @PathVariable("seasonNumber") int seasonNumber, @PathVariable("episodeNumber") int episodeNumber) {
        return catalogService.getEpisode(titleId, seasonNumber, episodeNumber);
    }

    @GetMapping("/genres")
    Map<String, List<Genre>> genres() {
        return Map.of("genres", catalogService.listGenres());
    }

    @GetMapping("/search")
    PageResponse<VideoTitle> search(@RequestParam("q") String q, @RequestParam(name = "genre", required = false) String genre) {
        List<VideoTitle> data = catalogService.listTitles(null, genre, q);
        return new PageResponse<>(data, Map.of("hasMore", false, "total", data.size()));
    }

    @GetMapping("/search/suggestions")
    Map<String, Object> suggestions(@RequestParam("q") String q) {
        List<Map<String, String>> suggestions = catalogService.listTitles(null, null, q).stream()
                .limit(10)
                .map(title -> Map.of("text", title.getTitle(), "type", "TITLE"))
                .toList();
        return Map.of("suggestions", suggestions);
    }
}
