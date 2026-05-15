package com.optimum.optimum.repository;

import com.optimum.optimum.model.SubscriptionPlan;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, UUID> {
}
