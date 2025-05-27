package com.example.subcontracting.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillOfMaterial {
    private Product finishedGood;
    private List<BomComponent> components;
}