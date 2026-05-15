package com.optimum.optimum.controller;

import com.optimum.optimum.model.NotificationItem;
import com.optimum.optimum.model.UserProfile;
import com.optimum.optimum.service.NotificationService;
import com.optimum.optimum.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationRestController {

    private final NotificationService notificationService;
    private final ProfileService profileService;

    public NotificationRestController(NotificationService notificationService, ProfileService profileService) {
        this.notificationService = notificationService;
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getNotifications() {
        UserProfile profile = profileService.demoProfile();
        List<NotificationItem> notifications = notificationService.listNotifications(profile);
        long unreadCount = notificationService.countUnread(profile);
        
        return ResponseEntity.ok(Map.of(
                "notifications", notifications,
                "unreadCount", unreadCount
        ));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllRead() {
        notificationService.markAllAsRead(profileService.demoProfile());
        return ResponseEntity.noContent().build();
    }
}
