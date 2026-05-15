package com.optimum.optimum.repository;

import com.optimum.optimum.model.UserAccount;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
    Optional<UserAccount> findByEmail(String email);

    Optional<UserAccount> findByUsername(String username);
}
