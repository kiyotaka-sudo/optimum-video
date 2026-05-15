package com.optimum.optimum.controller;

import com.optimum.optimum.service.SubscriptionService;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionRestController {
    private final SubscriptionService subscriptionService;

    public SubscriptionRestController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/plans")
    Map<String, Object> plans() {
        return Map.of("plans", subscriptionService.listPlans());
    }

    @GetMapping
    Map<String, Object> activeSubscription() {
        return Map.of("status", "ACTIVE", "billingCycle", "MONTHLY", "plan", "Premium");
    }

    @PostMapping
    ResponseEntity<Map<String, Object>> subscribe() {
        return ResponseEntity.status(HttpStatus.CREATED).body(activeSubscription());
    }

    @PostMapping("/cancel")
    Map<String, Object> cancel() {
        return Map.of("status", "ACTIVE", "cancelAtPeriodEnd", true);
    }
}
