// src/main/java/com/example/subcontracting/service/InMemoryDataStore.java
package com.example.subcontracting.service;

import com.example.subcontracting.model.BillOfMaterial;
import com.example.subcontracting.model.BomComponent;
import com.example.subcontracting.model.PairingRule; // New import
import com.example.subcontracting.model.Product;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList; // New import
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List; // New import
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryDataStore {

    private final Map<String, Product> products = new HashMap<>();
    private final Map<String, BillOfMaterial> boms = new HashMap<>();
    private final List<PairingRule> pairingRules = new ArrayList<>(); // New list for pairing rules

    // ... (Product ID constants and Product definitions remain the same) ...
    public static final String ID_RAW_MATERIAL_DOOR_CORE = "RM_CORE_001";
    public static final String ID_RAW_MATERIAL_HINGE = "RM_HINGE_002";
    public static final String ID_FG_DOOR_L = "FG_DOOR_L";
    public static final String ID_FG_DOOR_R = "FG_DOOR_R";
    public static final String ID_FG_DOOR_PAIR = "FG_DOOR_PAIR";

    public static final Product RAW_MATERIAL_DOOR_CORE = new Product(ID_RAW_MATERIAL_DOOR_CORE, "门芯原材料", Product.ProductType.RAW_MATERIAL);
    public static final Product RAW_MATERIAL_HINGE = new Product(ID_RAW_MATERIAL_HINGE, "铰链", Product.ProductType.RAW_MATERIAL);

    public static final Product FG_DOOR_L = new Product(ID_FG_DOOR_L, "左门 (委外成品)", Product.ProductType.FINISHED_GOOD);
    public static final Product FG_DOOR_R = new Product(ID_FG_DOOR_R, "右门 (委外成品)", Product.ProductType.FINISHED_GOOD);
    public static final Product FG_DOOR_PAIR = new Product(ID_FG_DOOR_PAIR, "左右门组合 (委外成品)", Product.ProductType.FINISHED_GOOD);


    @PostConstruct
    public void init() {
        // Add products to store
        products.put(RAW_MATERIAL_DOOR_CORE.getId(), RAW_MATERIAL_DOOR_CORE);
        products.put(RAW_MATERIAL_HINGE.getId(), RAW_MATERIAL_HINGE);
        products.put(FG_DOOR_L.getId(), FG_DOOR_L);
        products.put(FG_DOOR_R.getId(), FG_DOOR_R);
        products.put(FG_DOOR_PAIR.getId(), FG_DOOR_PAIR);

        // Define BOMs
        BillOfMaterial bomDoorL = new BillOfMaterial(FG_DOOR_L, Arrays.asList(
                new BomComponent(RAW_MATERIAL_DOOR_CORE, 1),
                new BomComponent(RAW_MATERIAL_HINGE, 2)
        ));
        boms.put(FG_DOOR_L.getId(), bomDoorL);

        BillOfMaterial bomDoorR = new BillOfMaterial(FG_DOOR_R, Arrays.asList(
                new BomComponent(RAW_MATERIAL_DOOR_CORE, 1),
                new BomComponent(RAW_MATERIAL_HINGE, 2)
        ));
        boms.put(FG_DOOR_R.getId(), bomDoorR);

        BillOfMaterial bomDoorPair = new BillOfMaterial(FG_DOOR_PAIR, Arrays.asList(
                new BomComponent(RAW_MATERIAL_DOOR_CORE, 1),
                new BomComponent(RAW_MATERIAL_HINGE, 4)
        ));
        boms.put(FG_DOOR_PAIR.getId(), bomDoorPair);

        // Define Pairing Rules
        // Rule: FG_DOOR_L and FG_DOOR_R can be combined into FG_DOOR_PAIR
        pairingRules.add(new PairingRule(ID_FG_DOOR_PAIR, ID_FG_DOOR_L, ID_FG_DOOR_R));
        // You could add more rules here if needed for other product combinations
        // e.g., pairingRules.add(new PairingRule("COMBO_WHEEL_SET", "WHEEL_FRONT", "WHEEL_BACK"));
    }

    public Optional<Product> findProductById(String productId) {
        return Optional.ofNullable(products.get(productId));
    }

    public Optional<BillOfMaterial> findBomByFinishedGoodId(String finishedGoodId) {
        return Optional.ofNullable(boms.get(finishedGoodId));
    }

    public List<PairingRule> getPairingRules() { // Getter for the rules
        return Collections.unmodifiableList(pairingRules);
    }
}
