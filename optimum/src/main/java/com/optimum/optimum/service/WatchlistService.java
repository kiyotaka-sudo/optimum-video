package com.optimum.optimum.service;

import com.optimum.optimum.model.VideoTitle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface WatchlistService {
    Page<VideoTitle> getWatchlist(UUID profileId, Pageable pageable);
    void addToWatchlist(UUID profileId, UUID titleId);
    void removeFromWatchlist(UUID profileId, UUID titleId);
    boolean isInWatchlist(UUID profileId, UUID titleId);
}
