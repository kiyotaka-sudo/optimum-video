package com.optimum.optimum.controller;

import com.optimum.optimum.dto.ApiDtos.PlaybackSession;
import com.optimum.optimum.dto.ApiDtos.PlaybackSessionRequest;
import com.optimum.optimum.dto.ApiDtos.ProgressUpdate;
import com.optimum.optimum.service.PlaybackService;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/playback")
public class PlaybackRestController {
    private final PlaybackService playbackService;

    public PlaybackRestController(PlaybackService playbackService) {
        this.playbackService = playbackService;
    }

    @PostMapping("/sessions")
    ResponseEntity<PlaybackSession> session(@RequestBody PlaybackSessionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playbackService.createSession(request));
    }

    @DeleteMapping("/sessions/{sessionId}")
    ResponseEntity<Void> terminate() {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sessions/active")
    Map<String, Object> activeSessions() {
        return Map.of("sessions", java.util.List.of(), "maxConcurrentStreams", 4);
    }

    @PutMapping("/progress")
    ResponseEntity<Void> progress(@RequestBody ProgressUpdate request) {
        playbackService.saveProgress(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/history")
    Map<String, Object> history() {
        return Map.of("data", playbackService.continueWatching());
    }
}
