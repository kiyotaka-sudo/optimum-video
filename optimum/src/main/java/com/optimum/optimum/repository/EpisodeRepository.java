package com.optimum.optimum.repository;

import com.optimum.optimum.model.Episode;
import com.optimum.optimum.model.VideoTitle;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpisodeRepository extends JpaRepository<Episode, UUID> {
    List<Episode> findBySeriesAndSeasonNumberOrderByEpisodeNumber(VideoTitle series, int seasonNumber);

    Optional<Episode> findBySeriesAndSeasonNumberAndEpisodeNumber(VideoTitle series, int seasonNumber, int episodeNumber);

    // Helper to list episodes by series id ordered by season and episode
    List<Episode> findBySeriesIdOrderBySeasonNumberAscEpisodeNumberAsc(UUID seriesId);
}
