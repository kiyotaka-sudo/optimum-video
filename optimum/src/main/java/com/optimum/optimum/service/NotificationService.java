package com.optimum.optimum.service;

import com.optimum.optimum.model.NotificationItem;
import com.optimum.optimum.model.UserProfile;
import java.util.List;
import java.util.UUID;

public interface NotificationService {
    List<NotificationItem> listNotifications(UserProfile profile);
    long countUnread(UserProfile profile);
    void markAsRead(UUID id);
    void markAllAsRead(UserProfile profile);
    NotificationItem createNotification(UserProfile profile, String type, String title, String body);
}
