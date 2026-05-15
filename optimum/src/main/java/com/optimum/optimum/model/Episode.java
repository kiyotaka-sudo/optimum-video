package com.optimum.optimum.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
public class Episode extends BaseEntity {
    @ManyToOne
    private VideoTitle series;
    private int seasonNumber;
    private int episodeNumber;
    private String title;
    private String synopsis;
    private int durationMinutes;
    private String thumbnailUrl;
    private LocalDate airDate;
    private String videoUrl;

    protected Episode() {
    }

    public Episode(VideoTitle series, int seasonNumber, int episodeNumber, String title, String synopsis, int durationMinutes, String thumbnailUrl) {
        this.series = series;
        this.seasonNumber = seasonNumber;
        this.episodeNumber = episodeNumber;
        this.title = title;
        this.synopsis = synopsis;
        this.durationMinutes = durationMinutes;
        this.thumbnailUrl = thumbnailUrl;
        this.airDate = LocalDate.now().minusDays(episodeNumber * 8L);
    }

    public VideoTitle getSeries() {
        return series;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public String getTitle() {
        return title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public LocalDate getAirDate() {
        return airDate;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
