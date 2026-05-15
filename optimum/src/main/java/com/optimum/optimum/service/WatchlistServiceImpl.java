package com.optimum.optimum.service;

import com.optimum.optimum.model.UserProfile;
import com.optimum.optimum.model.VideoTitle;
import com.optimum.optimum.model.WatchlistItem;
import com.optimum.optimum.repository.UserProfileRepository;
import com.optimum.optimum.repository.VideoTitleRepository;
import com.optimum.optimum.repository.WatchlistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class WatchlistServiceImpl implements WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final UserProfileRepository userProfileRepository;
    private final VideoTitleRepository videoTitleRepository;

    public WatchlistServiceImpl(WatchlistRepository watchlistRepository, 
                                UserProfileRepository userProfileRepository, 
                                VideoTitleRepository videoTitleRepository) {
        this.watchlistRepository = watchlistRepository;
        this.userProfileRepository = userProfileRepository;
        this.videoTitleRepository = videoTitleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VideoTitle> getWatchlist(UUID profileId, Pageable pageable) {
        return watchlistRepository.findByProfileIdOrderByAddedAtDesc(profileId, pageable)
                .map(WatchlistItem::getTitle);
    }

    @Override
    public void addToWatchlist(UUID profileId, UUID titleId) {
        if (watchlistRepository.existsByProfileIdAndTitleId(profileId, titleId)) {
            return;
        }

        UserProfile profile = userProfileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profil non trouvé : " + profileId));
        VideoTitle title = videoTitleRepository.findById(titleId)
                .orElseThrow(() -> new RuntimeException("Titre non trouvé : " + titleId));

        WatchlistItem item = new WatchlistItem(profile, title);
        watchlistRepository.save(item);
    }

    @Override
    public void removeFromWatchlist(UUID profileId, UUID titleId) {
        watchlistRepository.deleteByProfileIdAndTitleId(profileId, titleId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInWatchlist(UUID profileId, UUID titleId) {
        return watchlistRepository.existsByProfileIdAndTitleId(profileId, titleId);
    }
}
