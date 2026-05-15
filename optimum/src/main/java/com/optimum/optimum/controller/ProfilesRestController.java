package com.optimum.optimum.controller;

import com.optimum.optimum.model.UserProfile;
import com.optimum.optimum.service.ProfileService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profiles")
public class ProfilesRestController {
    private final ProfileService profileService;

    public ProfilesRestController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    Map<String, Object> profiles() {
        List<UserProfile> profiles = profileService.listProfiles();
        return Map.of("profiles", profiles, "maxProfiles", 6);
    }

    @PostMapping
    ResponseEntity<UserProfile> create(@RequestBody Map<String, Object> body) {
        String name = String.valueOf(body.getOrDefault("name", "Nouveau"));
        boolean kid = Boolean.parseBoolean(String.valueOf(body.getOrDefault("isKid", "false")));
        return ResponseEntity.status(HttpStatus.CREATED).body(profileService.createProfile(name, kid));
    }

    @GetMapping("/{profileId}")
    UserProfile profile(@PathVariable("profileId") UUID profileId) {
        return profileService.getProfile(profileId);
    }

    @PostMapping("/{profileId}/pin/verify")
    Map<String, Object> verifyPin(@PathVariable("profileId") UUID profileId, @RequestBody Map<String, String> body) {
        return Map.of("profile_session_token", "profile-" + profileId, "expires_in", 14400);
    }
}
