package com.optimum.optimum.model;

import jakarta.persistence.Entity;

@Entity
public class LiveChannel extends BaseEntity {
    private String name;
    private String country;
    private String category;
    private String logoUrl;
    private String streamUrl;
    private boolean premium;

    protected LiveChannel() {
    }

    public LiveChannel(String name, String country, String category, String logoUrl, String streamUrl, boolean premium) {
        this.name = name;
        this.country = country;
        this.category = category;
        this.logoUrl = logoUrl;
        this.streamUrl = streamUrl;
        this.premium = premium;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getCategory() {
        return category;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public boolean isPremium() {
        return premium;
    }
}
