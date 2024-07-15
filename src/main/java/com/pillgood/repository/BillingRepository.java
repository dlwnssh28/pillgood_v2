package com.pillgood.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pillgood.entity.Billing;

public interface BillingRepository extends JpaRepository<Billing, String> {
    Optional<Billing> findByMemberUniqueId(String memberUniqueId);
}
