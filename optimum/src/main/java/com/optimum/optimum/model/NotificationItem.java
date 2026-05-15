package com.optimum.optimum.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class NotificationItem extends BaseEntity {
    @ManyToOne
    private UserProfile profile;
    private String type;
    private String title;
    private String body;
    private String imageUrl;
    
    @jakarta.persistence.Column(name = "is_read")
    private boolean read;

    protected NotificationItem() {

    }

    public NotificationItem(UserProfile profile, String type, String title, String body) {
        this.profile = profile;
        this.type = type;
        this.title = title;
        this.body = body;
    }

    public void markRead() {
        read = true;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isRead() {
        return read;
    }
}
