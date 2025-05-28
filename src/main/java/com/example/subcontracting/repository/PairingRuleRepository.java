package com.example.subcontracting.repository;

import com.example.subcontracting.model.PairingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PairingRuleRepository extends JpaRepository<PairingRule, Long> {
    // You can add custom query methods here if needed
    // For example: List<PairingRule> findByPairProductId(String pairProductId);
}