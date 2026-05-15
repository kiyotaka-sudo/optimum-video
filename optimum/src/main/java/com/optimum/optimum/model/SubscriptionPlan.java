package com.optimum.optimum.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SubscriptionPlan extends BaseEntity {
    private String name;
    private double priceMonthly;
    private double priceYearly;
    private String currency;
    private int maxConcurrentStreams;
    private int maxProfiles;
    private boolean offlineDownloads;
    private int trialDays;
    @ElementCollection(fetch = jakarta.persistence.FetchType.EAGER)
    private List<String> features = new ArrayList<>();


    protected SubscriptionPlan() {
    }

    public SubscriptionPlan(String name, double priceMonthly, double priceYearly, int maxConcurrentStreams, int maxProfiles, boolean offlineDownloads) {
        this.name = name;
        this.priceMonthly = priceMonthly;
        this.priceYearly = priceYearly;
        this.maxConcurrentStreams = maxConcurrentStreams;
        this.maxProfiles = maxProfiles;
        this.offlineDownloads = offlineDownloads;
        this.currency = "EUR";
        this.trialDays = 7;
    }

    public SubscriptionPlan addFeature(String feature) {
        features.add(feature);
        return this;
    }

    public String getName() {
        return name;
    }

    public double getPriceMonthly() {
        return priceMonthly;
    }

    public double getPriceYearly() {
        return priceYearly;
    }

    public String getCurrency() {
        return currency;
    }

    public int getMaxConcurrentStreams() {
        return maxConcurrentStreams;
    }

    public int getMaxProfiles() {
        return maxProfiles;
    }

    public boolean isOfflineDownloads() {
        return offlineDownloads;
    }

    public int getTrialDays() {
        return trialDays;
    }

    public List<String> getFeatures() {
        return features;
    }
}
