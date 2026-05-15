package com.optimum.optimum.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinColumn;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class VideoTitle extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private ContentType type;
    private String title;
    private String originalTitle;
    private int releaseYear;
    private Integer durationMinutes;
    private String posterUrl;
    private String backdropUrl;
    private double rating;
    @Enumerated(EnumType.STRING)
    private MaturityRating maturityRating;
    private LocalDate availableUntil;
    private boolean newRelease;
    private boolean optimumOriginal;
    private String synopsis;
    private String language;
    private String trailerUrl;
    private Integer seasonCount;
    private int totalRatingVotes;
    private String videoUrl;
    private boolean isAfricanContent;


    @ManyToMany(fetch = FetchType.EAGER)
    private List<Genre> genres = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "video_title_cast_members", joinColumns = @JoinColumn(name = "video_title_id"))
    @Column(name = "actor_name")
    private List<String> cast = new ArrayList<>();

    protected VideoTitle() {
    }

    public VideoTitle(ContentType type, String title, int releaseYear, Integer durationMinutes, String posterUrl,
                      String backdropUrl, double rating, MaturityRating maturityRating, String synopsis) {
        this.type = type;
        setTitle(title);
        this.originalTitle = title;
        this.releaseYear = releaseYear;
        this.durationMinutes = durationMinutes;
        this.posterUrl = posterUrl;
        this.backdropUrl = backdropUrl;
        this.rating = rating;
        this.maturityRating = maturityRating;
        this.synopsis = synopsis;
        this.language = "fr";
        this.totalRatingVotes = 1200;
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void addCast(String name) {
        cast.add(name);
    }

    public ContentType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Le titre est obligatoire");
        }
        this.title = title.trim();
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getBackdropUrl() {
        return backdropUrl;
    }

    public double getRating() {
        return rating;
    }

    public MaturityRating getMaturityRating() {
        return maturityRating;
    }

    public LocalDate getAvailableUntil() {
        return availableUntil;
    }

    public boolean isNewRelease() {
        return newRelease;
    }

    public boolean isOptimumOriginal() {
        return optimumOriginal;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getLanguage() {
        return language;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public Integer getSeasonCount() {
        return seasonCount;
    }

    public int getTotalRatingVotes() {
        return totalRatingVotes;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public boolean isAfricanContent() {
        return isAfricanContent;
    }

    public void setAfricanContent(boolean isAfricanContent) {
        this.isAfricanContent = isAfricanContent;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public List<String> getCast() {
        return cast;
    }

    public VideoTitle markNew() {
        this.newRelease = true;
        return this;
    }

    public VideoTitle markOriginal() {
        this.optimumOriginal = true;
        return this;
    }

    public VideoTitle withSeriesInfo(int seasonCount) {
        this.seasonCount = seasonCount;
        return this;
    }
}
