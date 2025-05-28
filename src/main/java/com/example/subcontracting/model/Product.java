package com.example.subcontracting.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
public class Product {
    @Id
    private String id;
    private String name;

    @Enumerated(EnumType.STRING)
    private ProductType type;

    public enum ProductType {
        FINISHED_GOOD,
        RAW_MATERIAL
    }
}