// src/main/java/com/example/subcontracting/service/SubcontractingService.java
package com.example.subcontracting.service;

import com.example.subcontracting.dto.MaterialRequirementDto;
import com.example.subcontracting.dto.OrderItemDto;
import com.example.subcontracting.dto.OrderRequestDto;
import com.example.subcontracting.model.BillOfMaterial;
import com.example.subcontracting.model.BomComponent;
import com.example.subcontracting.model.PairingRule; // New import
import com.example.subcontracting.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SubcontractingService {

    private final InMemoryDataStore dataStore;

    @Autowired
    public SubcontractingService(InMemoryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    private List<OrderItemDto> optimizeOrderItems(List<OrderItemDto> originalItems) {
        // Create a mutable map of current quantities for each product ID in the order
        Map<String, Integer> currentQuantities = new HashMap<>();
        for (OrderItemDto item : originalItems) {
            currentQuantities.merge(item.getProductId(), item.getQuantity(), Integer::sum);
        }
    
        List<OrderItemDto> optimizedItems = new ArrayList<>();
    
        // Apply pairing rules
        for (PairingRule rule : dataStore.getPairingRules()) {
            String child1Id = rule.getChild1ProductId();
            String child2Id = rule.getChild2ProductId();
            String pairId = rule.getPairProductId();
            PairingRule.PairingType type = rule.getType();
    
            int qtyChild1 = currentQuantities.getOrDefault(child1Id, 0);
            int qtyChild2 = currentQuantities.getOrDefault(child2Id, 0);
    
            int numberOfPairs = 0;
            int consumedChild1 = 0;
            int consumedChild2 = 0;
    
            if (type == PairingRule.PairingType.MIN) {
                numberOfPairs = Math.min(qtyChild1, qtyChild2);
                consumedChild1 = numberOfPairs;
                consumedChild2 = numberOfPairs;
            } else if (type == PairingRule.PairingType.MAX) {
                if (qtyChild1 > 0 || qtyChild2 > 0) { // Only form pairs if at least one child exists
                    numberOfPairs = Math.max(qtyChild1, qtyChild2);
                    // For MAX, we assume the pair can be formed up to the max quantity of either child.
                    // The BOM for the pair product should reflect that it can be made even if one component is "virtually" supplied.
                    // Or, the cost/material implication is handled differently for MAX pairs.
                    // Here, we consume up to the available quantity of each child to form these MAX pairs.
                    consumedChild1 = Math.min(qtyChild1, numberOfPairs); 
                    consumedChild2 = Math.min(qtyChild2, numberOfPairs);
                    // If one child's quantity is less than numberOfPairs, it means we are forming pairs
                    // using all of that child and up to numberOfPairs of the other child.
                    // The crucial part is that `numberOfPairs` defines how many `pairId` items are created.
                } else {
                    numberOfPairs = 0;
                }
            }
    
            if (numberOfPairs > 0) {
                OrderItemDto pairItem = new OrderItemDto();
                pairItem.setProductId(pairId);
                pairItem.setQuantity(numberOfPairs);
                optimizedItems.add(pairItem);
    
                currentQuantities.put(child1Id, qtyChild1 - consumedChild1);
                currentQuantities.put(child2Id, qtyChild2 - consumedChild2);
            }
        }
    
        // Add remaining individual items (those not fully consumed by pairing or not part of any rule)
        currentQuantities.forEach((productId, quantity) -> {
            if (quantity > 0) {
                OrderItemDto remainingItem = new OrderItemDto();
                remainingItem.setProductId(productId);
                remainingItem.setQuantity(quantity);
                optimizedItems.add(remainingItem);
            }
        });
        
        // Ensure no product is listed twice, sum quantities if necessary (though current logic should prevent this)
        // This can be a safeguard or handle cases where originalItems had duplicates
        return optimizedItems.stream()
                .collect(Collectors.groupingBy(OrderItemDto::getProductId,
                         Collectors.summingInt(OrderItemDto::getQuantity)))
                .entrySet().stream()
                .map(entry -> {
                    OrderItemDto item = new OrderItemDto();
                    item.setProductId(entry.getKey());
                    item.setQuantity(entry.getValue());
                    return item;
                })
                .filter(item -> item.getQuantity() > 0) // Filter out zero quantity items
                .collect(Collectors.toList());
    }

    public List<MaterialRequirementDto> calculateRequiredMaterials(OrderRequestDto orderRequest) {
        // Optimize order items first
        List<OrderItemDto> optimizedOrderItems = optimizeOrderItems(orderRequest.getItems());

        // Log the optimized order for verification (optional)
        System.out.println("Optimized Order Items: " + optimizedOrderItems);


        Map<Product, Integer> totalRawMaterialsNeeded = new HashMap<>();

        for (OrderItemDto itemDto : optimizedOrderItems) {
            Product finishedGood = dataStore.findProductById(itemDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + itemDto.getProductId()));

            if (finishedGood.getType() != Product.ProductType.FINISHED_GOOD) {
                // This could happen if a raw material ID was somehow in optimizedOrderItems directly,
                // though current optimizeOrderItems logic focuses on finished goods.
                System.out.println("Warning: Item " + itemDto.getProductId() + " is not a finished good. Skipping BOM explosion.");
                continue;
            }

            BillOfMaterial bom = dataStore.findBomByFinishedGoodId(finishedGood.getId())
                    .orElseThrow(() -> new IllegalArgumentException("BOM not found for: " + finishedGood.getName()));

            for (BomComponent component : bom.getComponents()) {
                if (component.getMaterial().getType() == Product.ProductType.RAW_MATERIAL) {
                    totalRawMaterialsNeeded.merge(
                            component.getMaterial(),
                            component.getQuantity() * itemDto.getQuantity(),
                            Integer::sum
                    );
                } else {
                    System.out.println("Warning: BOM component " + component.getMaterial().getName() +
                                       " is not a raw material or is a sub-assembly. " +
                                       "Current logic only processes direct raw materials from top-level BOM.");
                }
            }
        }

        List<MaterialRequirementDto> result = new ArrayList<>();
        totalRawMaterialsNeeded.forEach((material, quantity) ->
                result.add(new MaterialRequirementDto(material.getId(), material.getName(), quantity))
        );
        return result;
    }
}
