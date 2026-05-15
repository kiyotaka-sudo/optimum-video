package com.optimum.optimum.repository;

import com.optimum.optimum.model.UserAccount;
import com.optimum.optimum.model.UserProfile;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    List<UserProfile> findByAccount(UserAccount account);
}
