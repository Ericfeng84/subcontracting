// src/main/java/com/example/subcontracting/model/PairingRule.java
package com.example.subcontracting.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Data
// 删除@NoArgsConstructor注解，因为已经有了显式的无参构造函数
// 移除@AllArgsConstructor注解，因为已经有了显式的构造函数
@Entity
public class PairingRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pairProductId;
    private String child1ProductId;
    private String child2ProductId;

    @Enumerated(EnumType.STRING)
    private PairingType type; // New field for pairing type

    public enum PairingType {
        MIN, // Existing logic: min(child1, child2)
        MAX  // New logic: max(child1, child2)
    }

    // Constructor to include type
    public PairingRule(Long id, String pairProductId, String child1ProductId, String child2ProductId, PairingType type) {
        this.id = id;
        this.pairProductId = pairProductId;
        this.child1ProductId = child1ProductId;
        this.child2ProductId = child2ProductId;
        this.type = type;
    }

    // Default constructor for JPA
    public PairingRule() {
    }

    // The getChildProductIds method is fine as a transient utility method
    // It doesn't need to be persisted if child1ProductId and child2ProductId are stored.
    public Set<String> getChildProductIds() {
        return new HashSet<>(Arrays.asList(child1ProductId, child2ProductId));
    }
}
