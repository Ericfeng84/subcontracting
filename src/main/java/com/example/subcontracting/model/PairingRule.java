// src/main/java/com/example/subcontracting/model/PairingRule.java
package com.example.subcontracting.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PairingRule {
    private String pairProductId; // The ID of the resulting "pair" or "combo" product (e.g., FG_DOOR_PAIR)
    private String child1ProductId; // ID of the first component (e.g., FG_DOOR_L)
    private String child2ProductId; // ID of the second component (e.g., FG_DOOR_R)
    // In a more generic system, childProductIds could be a List<String>

    public Set<String> getChildProductIds() {
        return new HashSet<>(Arrays.asList(child1ProductId, child2ProductId));
    }
}
