package com.optimum.optimum.service;

import com.optimum.optimum.dto.ApiDtos.PlaybackSession;
import com.optimum.optimum.dto.ApiDtos.PlaybackSessionRequest;
import com.optimum.optimum.dto.ApiDtos.ProgressUpdate;
import com.optimum.optimum.model.PlaybackProgress;
import com.optimum.optimum.model.UserProfile;
import com.optimum.optimum.model.VideoTitle;
import com.optimum.optimum.repository.PlaybackProgressRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlaybackService {
    private final CatalogService catalogService;
    private final ProfileService profileService;
    private final PlaybackProgressRepository progressRepository;
    private final com.optimum.optimum.repository.EpisodeRepository episodeRepository;

    public PlaybackService(CatalogService catalogService, ProfileService profileService, PlaybackProgressRepository progressRepository,
                           com.optimum.optimum.repository.EpisodeRepository episodeRepository) {
        this.catalogService = catalogService;
        this.profileService = profileService;
        this.progressRepository = progressRepository;
        this.episodeRepository = episodeRepository;
    }

    public PlaybackSession createSession(PlaybackSessionRequest request) {
        String streamUrl = "https://stream.optimum.video/hls/" + request.contentId() + "/master.m3u8";

        // If contentType is EPISODE, try to resolve episode videoUrl first
        if (request.contentType() != null && "EPISODE".equalsIgnoreCase(request.contentType())) {
            try {
                java.util.UUID epId = java.util.UUID.fromString(request.contentId());
                var episodeOpt = episodeRepository.findById(epId);
                if (episodeOpt.isPresent()) {
                    var ep = episodeOpt.get();
                    if (ep.getVideoUrl() != null && !ep.getVideoUrl().isBlank()) {
                        streamUrl = ep.getVideoUrl();
                    } else if (ep.getSeries() != null && ep.getSeries().getVideoUrl() != null && !ep.getSeries().getVideoUrl().isBlank()) {
                        streamUrl = ep.getSeries().getVideoUrl();
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // fallback to title-based lookup below
            }
        } else {
            VideoTitle title = catalogService.getTitle(UUID.fromString(request.contentId()));
            if (title != null && title.getVideoUrl() != null && !title.getVideoUrl().isBlank()) {
                streamUrl = title.getVideoUrl();
            }
        }

        int resume = request.resumeFromSeconds() == null ? 0 : request.resumeFromSeconds();
        return new PlaybackSession(
                UUID.randomUUID().toString(),
                streamUrl,
                "HLS",
                resume,
                LocalDateTime.now().plusHours(4).toString(),
                Map.of("system", request.drmSystem() == null ? "NONE" : request.drmSystem(), "token", UUID.randomUUID().toString())
        );
    }

    @Transactional
    public void saveProgress(ProgressUpdate request) {
        UserProfile profile = profileService.getProfile(UUID.fromString(request.profileId()));
        VideoTitle title = catalogService.getTitle(UUID.fromString(request.contentId()));
        PlaybackProgress progress = progressRepository.findByProfileAndTitle(profile, title)
                .orElseGet(() -> new PlaybackProgress(profile, title, 0, request.durationSeconds()));
        progress.update(request.positionSeconds(), request.durationSeconds(), request.completed());
        progressRepository.save(progress);
    }

    public List<PlaybackProgress> continueWatching() {
        return progressRepository.findTop8ByProfileOrderByLastWatchedAtDesc(profileService.demoProfile());
    }
}
