package com.optimum.optimum.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "watchlist_items")
public class WatchlistItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private UserProfile profile;

    @ManyToOne
    @JoinColumn(name = "title_id", nullable = false)
    private VideoTitle title;

    @Column(name = "added_at")
    private LocalDateTime addedAt = LocalDateTime.now();

    public WatchlistItem() {}

    public WatchlistItem(UserProfile profile, VideoTitle title) {
        this.profile = profile;
        this.title = title;
        this.addedAt = LocalDateTime.now();
    }

    public UserProfile getProfile() { return profile; }
    public void setProfile(UserProfile profile) { this.profile = profile; }
    public VideoTitle getTitle() { return title; }
    public void setTitle(VideoTitle title) { this.title = title; }
    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
}
