package com.optimum.optimum.repository;

import com.optimum.optimum.model.PlaybackProgress;
import com.optimum.optimum.model.UserProfile;
import com.optimum.optimum.model.VideoTitle;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaybackProgressRepository extends JpaRepository<PlaybackProgress, UUID> {
    List<PlaybackProgress> findTop8ByProfileOrderByLastWatchedAtDesc(UserProfile profile);

    Optional<PlaybackProgress> findByProfileAndTitle(UserProfile profile, VideoTitle title);
}
