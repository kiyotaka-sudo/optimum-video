package com.optimum.optimum.service;

import com.optimum.optimum.model.ContentType;
import com.optimum.optimum.model.Episode;
import com.optimum.optimum.model.Genre;
import com.optimum.optimum.model.VideoTitle;
import com.optimum.optimum.repository.EpisodeRepository;
import com.optimum.optimum.repository.GenreRepository;
import com.optimum.optimum.repository.VideoTitleRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {
    private final VideoTitleRepository titles;
    private final GenreRepository genres;
    private final EpisodeRepository episodes;

    public CatalogService(VideoTitleRepository titles, GenreRepository genres, EpisodeRepository episodes) {
        this.titles = titles;
        this.genres = genres;
        this.episodes = episodes;
    }

    public List<VideoTitle> listTitles(String type, String genre, String query) {
        if (type != null && !type.isBlank()) {
            return titles.findByType(ContentType.valueOf(type));
        }
        return titles.search(blankToNull(query), blankToNull(genre));
    }

    public VideoTitle getTitle(UUID id) {
        return titles.findById(id).orElseThrow(() -> new IllegalArgumentException("Titre introuvable"));
    }

    public List<Genre> listGenres() {
        return genres.findAll();
    }

    public List<Episode> listEpisodes(UUID titleId, int seasonNumber) {
        return episodes.findBySeriesAndSeasonNumberOrderByEpisodeNumber(getTitle(titleId), seasonNumber);
    }

    public Episode getEpisode(UUID titleId, int seasonNumber, int episodeNumber) {
        return episodes.findBySeriesAndSeasonNumberAndEpisodeNumber(getTitle(titleId), seasonNumber, episodeNumber)
                .orElseThrow(() -> new IllegalArgumentException("Episode introuvable"));
    }

    public List<VideoTitle> trending() {
        return titles.findTop8ByOrderByRatingDesc();
    }

    public List<VideoTitle> newReleases() {
        return titles.findTop8ByNewReleaseTrueOrderByReleaseYearDesc();
    }

    public List<VideoTitle> originals() {
        return titles.findTop8ByOptimumOriginalTrueOrderByRatingDesc();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
