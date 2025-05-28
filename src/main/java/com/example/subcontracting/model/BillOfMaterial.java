package com.example.subcontracting.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BillOfMaterial {
    // We need a unique ID for BillOfMaterial. We can use the finishedGood's ID if it's unique for each BOM.
    // Or generate a new one. For simplicity, let's assume finishedGood.id is the primary key for BOM.
    // If a finished good can have multiple BOMs (e.g., versions), this needs a different strategy.
    @Id
    private String id; // This will store the finishedGood.id

    @ManyToOne(fetch = FetchType.EAGER) // A BOM is for one finished good
    private Product finishedGood;

    @ElementCollection(fetch = FetchType.EAGER) // Store BomComponents as part of BillOfMaterial table or a separate collection table
    private List<BomComponent> components;
}