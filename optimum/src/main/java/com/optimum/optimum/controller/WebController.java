package com.optimum.optimum.controller;

import com.optimum.optimum.service.*;
import com.optimum.optimum.repository.SubscriptionPlanRepository;
import java.security.Principal;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebController {
    private final CatalogService catalogService;
    private final PlaybackService playbackService;
    private final ProfileService profileService;
    private final SubscriptionService subscriptionService;
    private final AuthService authService;
    private final PasswordPolicy passwordPolicy;
    private final AdminUserService adminUserService;
    private final SubscriptionPlanRepository planRepository;
    private final com.optimum.optimum.repository.UserAccountRepository accountRepository;

    public WebController(CatalogService catalogService, PlaybackService playbackService,
                         ProfileService profileService, SubscriptionService subscriptionService,
                         AuthService authService, PasswordPolicy passwordPolicy,
                         AdminUserService adminUserService,
                         SubscriptionPlanRepository planRepository,
                         com.optimum.optimum.repository.UserAccountRepository accountRepository) {
        this.catalogService = catalogService;
        this.playbackService = playbackService;
        this.profileService = profileService;
        this.subscriptionService = subscriptionService;
        this.authService = authService;
        this.passwordPolicy = passwordPolicy;
        this.adminUserService = adminUserService;
        this.planRepository = planRepository;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/login")
    String login() { return "login"; }

    @GetMapping("/register")
    String register(Model model) {
        model.addAttribute("errors", java.util.List.of());
        return "register";
    }

    @PostMapping("/register")
    String createAccount(@RequestParam("email") String email,
                         @RequestParam("password") String password,
                         @RequestParam("firstName") String firstName,
                         @RequestParam("lastName") String lastName,
                         Model model) {
        java.util.List<String> errors = passwordPolicy.violations(password);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "register";
        }
        authService.register(new com.optimum.optimum.dto.AuthDtos.RegisterRequest(email, password, firstName, lastName));
        return "redirect:/login?registered";
    }

    @GetMapping("/")
    String home(Model model) {
        model.addAttribute("hero", catalogService.trending().get(0));
        model.addAttribute("continueWatching", playbackService.continueWatching());
        model.addAttribute("trending", catalogService.trending());
        model.addAttribute("originals", catalogService.originals());
        model.addAttribute("newReleases", catalogService.newReleases());
        model.addAttribute("profiles", profileService.listProfiles());
        return "home";
    }

    @GetMapping("/admin")
    String admin(Model model, Principal principal) {
        model.addAttribute("email", principal == null ? "" : principal.getName());
        model.addAttribute("allTitles", catalogService.listTitles(null, null, ""));
        model.addAttribute("plans", subscriptionService.listPlans());
        model.addAttribute("users", adminUserService.listAll());
        model.addAttribute("userCount", accountRepository.count());
        model.addAttribute("titleCount", catalogService.listTitles(null, null, "").size());
        model.addAttribute("planCount", planRepository.count());
        return "admin";
    }

    @GetMapping("/titles/{id}")
    String details(@PathVariable("id") UUID id, Model model) {
        model.addAttribute("title", catalogService.getTitle(id));
        model.addAttribute("episodes", catalogService.listEpisodes(id, 1));
        model.addAttribute("profile", profileService.demoProfile());
        return "details";
    }

    @GetMapping("/search-ui")
    String search(@RequestParam(name = "q", required = false, defaultValue = "") String q, Model model) {
        model.addAttribute("query", q);
        model.addAttribute("results", catalogService.listTitles(null, null, q));
        return "search";
    }

    @GetMapping("/profiles-ui")
    String profiles(Model model) {
        model.addAttribute("profiles", profileService.listProfiles());
        return "profiles";
    }

    @GetMapping("/plans")
    String plans(Model model) {
        model.addAttribute("plans", subscriptionService.listPlans());
        return "plans";
    }

    @GetMapping("/watchlist")
    String watchlist(Model model) {
        model.addAttribute("profile", profileService.demoProfile());
        return "watchlist";
    }
}
