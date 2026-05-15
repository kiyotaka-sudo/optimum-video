package com.optimum.optimum.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;

@Entity
public class UserProfile extends BaseEntity {
    private String name;
    private String avatarUrl;
    private boolean kid;
    private String language;
    @Enumerated(EnumType.STRING)
    private MaturityRating maturityRating;
    private boolean pinEnabled;
    private boolean defaultProfile;

    @ManyToOne
    private UserAccount account;

    protected UserProfile() {
    }

    public UserProfile(UserAccount account, String name, boolean kid, MaturityRating maturityRating, boolean defaultProfile) {
        this.account = account;
        setName(name);
        this.kid = kid;
        this.language = "fr";
        this.maturityRating = maturityRating;
        this.defaultProfile = defaultProfile;
        this.avatarUrl = "https://api.dicebear.com/8.x/initials/svg?seed=" + name.replace(" ", "%20");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom du profil est obligatoire");
        }
        this.name = name.trim();
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public boolean isKid() {
        return kid;
    }

    public String getLanguage() {
        return language;
    }

    public MaturityRating getMaturityRating() {
        return maturityRating;
    }

    public boolean isPinEnabled() {
        return pinEnabled;
    }

    public boolean isDefaultProfile() {
        return defaultProfile;
    }

    public UserAccount getAccount() {
        return account;
    }
}
