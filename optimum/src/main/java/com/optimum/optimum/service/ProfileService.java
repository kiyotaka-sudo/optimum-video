package com.optimum.optimum.service;

import com.optimum.optimum.model.MaturityRating;
import com.optimum.optimum.model.UserAccount;
import com.optimum.optimum.model.UserProfile;
import com.optimum.optimum.repository.UserAccountRepository;
import com.optimum.optimum.repository.UserProfileRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {
    private final UserAccountRepository accounts;
    private final UserProfileRepository profiles;

    public ProfileService(UserAccountRepository accounts, UserProfileRepository profiles) {
        this.accounts = accounts;
        this.profiles = profiles;
    }

    public UserAccount demoAccount() {
        return accounts.findByEmail("demo@optimum.video").orElseThrow();
    }

    public UserProfile demoProfile() {
        return profiles.findByAccount(demoAccount()).stream().findFirst().orElseThrow();
    }

    public List<UserProfile> listProfiles() {
        return profiles.findByAccount(demoAccount());
    }

    @Transactional
    public UserProfile createProfile(String name, boolean kid) {
        return profiles.save(new UserProfile(demoAccount(), name, kid, kid ? MaturityRating.PG : MaturityRating.R, false));
    }

    public UserProfile getProfile(UUID id) {
        return profiles.findById(id).orElseThrow(() -> new IllegalArgumentException("Profil introuvable"));
    }
}
