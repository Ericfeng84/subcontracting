// src/main/java/com/example/subcontracting/service/InMemoryDataStore.java
package com.example.subcontracting.service;

import com.example.subcontracting.model.BillOfMaterial;
import com.example.subcontracting.model.BomComponent;
import com.example.subcontracting.model.PairingRule;
import com.example.subcontracting.model.Product;
import com.example.subcontracting.repository.BillOfMaterialRepository;
import com.example.subcontracting.repository.PairingRuleRepository; // New import
import com.example.subcontracting.repository.ProductRepository;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired; // New import

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List; // New import
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryDataStore {

    // Remove old List for pairingRules
    // private final List<PairingRule> pairingRules = new ArrayList<>();

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BillOfMaterialRepository billOfMaterialRepository;

    @Autowired // New dependency
    private PairingRuleRepository pairingRuleRepository;

    public static final String ID_RAW_MATERIAL_DOOR_CORE = "RM_CORE_001";
    public static final String ID_RAW_MATERIAL_HINGE = "RM_HINGE_002";
    public static final String ID_FG_DOOR_L = "FG_DOOR_L";
    public static final String ID_FG_DOOR_R = "FG_DOOR_R";
    public static final String ID_FG_DOOR_PAIR = "FG_DOOR_PAIR";

    // Define new Product IDs for MAX pairing
    public static final String ID_FG_DOOR_L_MAX = "FG_DOOR_L_MAX";
    public static final String ID_FG_DOOR_R_MAX = "FG_DOOR_R_MAX";
    public static final String ID_FG_DOOR_PAIR_MAX = "FG_DOOR_PAIR_MAX";

    public static final Product RAW_MATERIAL_DOOR_CORE = new Product(ID_RAW_MATERIAL_DOOR_CORE, "门芯原材料", Product.ProductType.RAW_MATERIAL);
    public static final Product RAW_MATERIAL_HINGE = new Product(ID_RAW_MATERIAL_HINGE, "铰链", Product.ProductType.RAW_MATERIAL);

    public static final Product FG_DOOR_L = new Product(ID_FG_DOOR_L, "左门 (委外成品)", Product.ProductType.FINISHED_GOOD);
    public static final Product FG_DOOR_R = new Product(ID_FG_DOOR_R, "右门 (委外成品)", Product.ProductType.FINISHED_GOOD);
    public static final Product FG_DOOR_PAIR = new Product(ID_FG_DOOR_PAIR, "左右门组合 (委外成品)", Product.ProductType.FINISHED_GOOD);

    // Define new Product instances for MAX pairing
    public static final Product FG_DOOR_L_MAX = new Product(ID_FG_DOOR_L_MAX, "左门 (MAX配对)", Product.ProductType.FINISHED_GOOD);
    public static final Product FG_DOOR_R_MAX = new Product(ID_FG_DOOR_R_MAX, "右门 (MAX配对)", Product.ProductType.FINISHED_GOOD);
    public static final Product FG_DOOR_PAIR_MAX = new Product(ID_FG_DOOR_PAIR_MAX, "左右门组合 (MAX配对)", Product.ProductType.FINISHED_GOOD);


    @PostConstruct
    public void init() {
        // Add products to store via repository
        Product rawMaterialDoorCore = new Product(ID_RAW_MATERIAL_DOOR_CORE, "门芯原材料", Product.ProductType.RAW_MATERIAL);
        Product rawMaterialHinge = new Product(ID_RAW_MATERIAL_HINGE, "铰链", Product.ProductType.RAW_MATERIAL);
        Product fgDoorL = new Product(ID_FG_DOOR_L, "左门 (委外成品)", Product.ProductType.FINISHED_GOOD);
        Product fgDoorR = new Product(ID_FG_DOOR_R, "右门 (委外成品)", Product.ProductType.FINISHED_GOOD);
        Product fgDoorPair = new Product(ID_FG_DOOR_PAIR, "左右门组合 (委外成品)", Product.ProductType.FINISHED_GOOD);

        // Add new MAX pairing products to repository
        Product fgDoorLMax = new Product(ID_FG_DOOR_L_MAX, "左门 (MAX配对)", Product.ProductType.FINISHED_GOOD);
        Product fgDoorRMax = new Product(ID_FG_DOOR_R_MAX, "右门 (MAX配对)", Product.ProductType.FINISHED_GOOD);
        Product fgDoorPairMax = new Product(ID_FG_DOOR_PAIR_MAX, "左右门组合 (MAX配对)", Product.ProductType.FINISHED_GOOD);

        productRepository.saveAll(Arrays.asList(rawMaterialDoorCore, rawMaterialHinge, fgDoorL, fgDoorR, fgDoorPair, fgDoorLMax, fgDoorRMax, fgDoorPairMax));

        // Define BOMs and save them
        BillOfMaterial bomDoorL = new BillOfMaterial(fgDoorL.getId(), fgDoorL, Arrays.asList(
                new BomComponent(rawMaterialDoorCore, 1),
                new BomComponent(rawMaterialHinge, 2)
        ));
        billOfMaterialRepository.save(bomDoorL);

        BillOfMaterial bomDoorR = new BillOfMaterial(fgDoorR.getId(), fgDoorR, Arrays.asList(
                new BomComponent(rawMaterialDoorCore, 1),
                new BomComponent(rawMaterialHinge, 2)
        ));
        billOfMaterialRepository.save(bomDoorR);

        BillOfMaterial bomDoorPair = new BillOfMaterial(fgDoorPair.getId(), fgDoorPair, Arrays.asList(
                new BomComponent(rawMaterialDoorCore, 1), // Assuming one core for the pair as per original logic
                new BomComponent(rawMaterialHinge, 4)
        ));
        billOfMaterialRepository.save(bomDoorPair);

        // Define BOMs for MAX pairing products
        BillOfMaterial bomDoorLMax = new BillOfMaterial(fgDoorLMax.getId(), fgDoorLMax, Arrays.asList(
                new BomComponent(rawMaterialDoorCore, 1),
                new BomComponent(rawMaterialHinge, 2)
        ));
        billOfMaterialRepository.save(bomDoorLMax);

        BillOfMaterial bomDoorRMax = new BillOfMaterial(fgDoorRMax.getId(), fgDoorRMax, Arrays.asList(
                new BomComponent(rawMaterialDoorCore, 1),
                new BomComponent(rawMaterialHinge, 2)
        ));
        billOfMaterialRepository.save(bomDoorRMax);

        // For MAX pairing, the combined BOM might be different or not strictly additive.
        // This example assumes it's similar to the MIN pair for simplicity, but could be adjusted based on business logic.
        // For example, if MAX pairing means taking all components from both, the quantities would be higher.
        // Or, if it's about fulfilling an order for the MAX quantity of *either* L or R, the BOM for FG_DOOR_PAIR_MAX might represent the components for *one* of them, 
        // and the pairing logic itself handles the quantity.
        // For now, let's assume FG_DOOR_PAIR_MAX also has a BOM, perhaps representing a 'virtual' paired item for tracking.
        BillOfMaterial bomDoorPairMax = new BillOfMaterial(fgDoorPairMax.getId(), fgDoorPairMax, Arrays.asList(
                new BomComponent(rawMaterialDoorCore, 1), // Example: still one core
                new BomComponent(rawMaterialHinge, 4)  // Example: still 4 hinges for a conceptual pair
        ));
        billOfMaterialRepository.save(bomDoorPairMax);

        // Define Pairing Rules and save them via repository
        // Original MIN rule
        PairingRule doorPairRuleMin = new PairingRule(null, ID_FG_DOOR_PAIR, Arrays.asList(ID_FG_DOOR_L, ID_FG_DOOR_R), PairingRule.PairingType.MIN);
        pairingRuleRepository.save(doorPairRuleMin);

        // New MAX rule example
        PairingRule doorPairRuleMax = new PairingRule(null, ID_FG_DOOR_PAIR_MAX, Arrays.asList(ID_FG_DOOR_L_MAX, ID_FG_DOOR_R_MAX), PairingRule.PairingType.MAX);
        pairingRuleRepository.save(doorPairRuleMax);
    }

    public Optional<Product> findProductById(String productId) {
        return productRepository.findById(productId);
    }

    public Optional<BillOfMaterial> findBomByFinishedGoodId(String finishedGoodId) {
        // Assuming BillOfMaterial's ID is the finishedGoodId
        // return billOfMaterialRepository.findById(finishedGoodId);
        // Or using the custom finder method:
        return billOfMaterialRepository.findByFinishedGoodId(finishedGoodId);
    }

    public List<PairingRule> getPairingRules() { // Getter for the rules
        return pairingRuleRepository.findAll(); // Retrieve all rules from the database
    }

    // 新增：获取所有产品
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
