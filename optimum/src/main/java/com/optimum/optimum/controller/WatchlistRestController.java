package com.optimum.optimum.controller;

import com.optimum.optimum.model.VideoTitle;
import com.optimum.optimum.service.ProfileService;
import com.optimum.optimum.service.WatchlistService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistRestController {

    private final WatchlistService watchlistService;
    private final ProfileService profileService;

    public WatchlistRestController(WatchlistService watchlistService, ProfileService profileService) {
        this.watchlistService = watchlistService;
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getWatchlist(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        UUID profileId = profileService.demoProfile().getId(); // Utilise le profil démo par défaut
        Pageable pageable = PageRequest.of(page, size);
        Page<VideoTitle> titles = watchlistService.getWatchlist(profileId, pageable);
        
        return ResponseEntity.ok(Map.of(
                "data", titles.getContent(),
                "totalElements", titles.getTotalElements(),
                "totalPages", titles.getTotalPages(),
                "page", titles.getNumber(),
                "size", titles.getSize()
        ));
    }

    @PutMapping("/{titleId}")
    public ResponseEntity<Void> add(@PathVariable UUID titleId) {
        watchlistService.addToWatchlist(profileService.demoProfile().getId(), titleId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{titleId}")
    public ResponseEntity<Void> remove(@PathVariable UUID titleId) {
        watchlistService.removeFromWatchlist(profileService.demoProfile().getId(), titleId);
        return ResponseEntity.noContent().build();
    }
}
