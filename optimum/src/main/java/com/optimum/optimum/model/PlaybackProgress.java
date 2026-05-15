package com.optimum.optimum.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class PlaybackProgress extends BaseEntity {
    @ManyToOne
    private UserProfile profile;
    @ManyToOne
    private VideoTitle title;
    private int positionSeconds;
    private int durationSeconds;
    private boolean completed;
    private LocalDateTime lastWatchedAt;

    protected PlaybackProgress() {
    }

    public PlaybackProgress(UserProfile profile, VideoTitle title, int positionSeconds, int durationSeconds) {
        this.profile = profile;
        this.title = title;
        update(positionSeconds, durationSeconds, false);
    }

    public void update(int positionSeconds, int durationSeconds, boolean completed) {
        if (positionSeconds < 0 || durationSeconds < 1) {
            throw new IllegalArgumentException("Progression invalide");
        }
        this.positionSeconds = positionSeconds;
        this.durationSeconds = durationSeconds;
        this.completed = completed;
        this.lastWatchedAt = LocalDateTime.now();
    }

    public UserProfile getProfile() {
        return profile;
    }

    public VideoTitle getTitle() {
        return title;
    }

    public int getPositionSeconds() {
        return positionSeconds;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public int getProgressPercent() {
        return Math.min(100, (int) Math.round((positionSeconds * 100.0) / durationSeconds));
    }

    public boolean isCompleted() {
        return completed;
    }

    public LocalDateTime getLastWatchedAt() {
        return lastWatchedAt;
    }
}
