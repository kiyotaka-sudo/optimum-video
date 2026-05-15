package com.optimum.optimum.service;

import com.optimum.optimum.model.NotificationItem;
import com.optimum.optimum.model.UserProfile;
import com.optimum.optimum.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;

    public NotificationServiceImpl(NotificationRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationItem> listNotifications(UserProfile profile) {
        return repository.findByProfileOrderByCreatedAtDesc(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread(UserProfile profile) {
        return repository.countByProfileAndReadFalse(profile);
    }

    @Override
    public void markAsRead(UUID id) {
        repository.findById(id).ifPresent(n -> {
            n.markRead();
            repository.save(n);
        });
    }

    @Override
    public void markAllAsRead(UserProfile profile) {
        List<NotificationItem> unread = repository.findByProfileOrderByCreatedAtDesc(profile)
                .stream().filter(n -> !n.isRead()).toList();
        unread.forEach(NotificationItem::markRead);
        repository.saveAll(unread);
    }

    @Override
    public NotificationItem createNotification(UserProfile profile, String type, String title, String body) {
        return repository.save(new NotificationItem(profile, type, title, body));
    }
}
