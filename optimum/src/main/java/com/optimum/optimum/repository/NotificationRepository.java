package com.optimum.optimum.repository;

import com.optimum.optimum.model.NotificationItem;
import com.optimum.optimum.model.UserProfile;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationItem, UUID> {
    List<NotificationItem> findByProfileOrderByCreatedAtDesc(UserProfile profile);

    long countByProfileAndReadFalse(UserProfile profile);
}
