// src/main/java/com/example/subcontracting/model/PairingRule.java
package com.example.subcontracting.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PairingRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Changed to IDENTITY
    private Long id;

    // We can link to actual Product entities if desired, or keep as String IDs
    // For simplicity and consistency with current structure, let's keep them as String IDs for now.
    // If linking to Product entities: @ManyToOne private Product pairProduct;
    private String pairProductId; 
    private String child1ProductId;
    private String child2ProductId;

    // The getChildProductIds method is fine as a transient utility method
    // It doesn't need to be persisted if child1ProductId and child2ProductId are stored.
    public Set<String> getChildProductIds() {
        return new HashSet<>(Arrays.asList(child1ProductId, child2ProductId));
    }
}
