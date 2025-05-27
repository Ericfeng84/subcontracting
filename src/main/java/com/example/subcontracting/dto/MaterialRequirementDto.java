package com.example.subcontracting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialRequirementDto {
    private String materialId;
    private String materialName;
    private int totalQuantityRequired;
}
