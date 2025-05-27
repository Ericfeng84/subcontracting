package com.example.subcontracting.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Product {
    private String id;
    private String name;
    private ProductType type;

    public enum ProductType {
        FINISHED_GOOD,
        RAW_MATERIAL
    }
}