package com.optimum.optimum.service;

import com.optimum.optimum.model.SubscriptionPlan;
import com.optimum.optimum.repository.SubscriptionPlanRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {
    private final SubscriptionPlanRepository plans;

    public SubscriptionService(SubscriptionPlanRepository plans) {
        this.plans = plans;
    }

    public List<SubscriptionPlan> listPlans() {
        return plans.findAll();
    }
}
