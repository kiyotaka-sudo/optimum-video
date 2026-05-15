package com.optimum.optimum.repository;

import com.optimum.optimum.model.WatchlistItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WatchlistRepository extends JpaRepository<WatchlistItem, UUID> {
    Page<WatchlistItem> findByProfileIdOrderByAddedAtDesc(UUID profileId, Pageable pageable);
    Optional<WatchlistItem> findByProfileIdAndTitleId(UUID profileId, UUID titleId);
    void deleteByProfileIdAndTitleId(UUID profileId, UUID titleId);
    boolean existsByProfileIdAndTitleId(UUID profileId, UUID titleId);
}
