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
            List<String> childIds = new ArrayList<>(rule.getChildProductIds());
            String pairId = rule.getPairProductId();
            PairingRule.PairingType type = rule.getType();
    
            // 获取所有子件的当前数量
            List<Integer> childQuantities = childIds.stream()
                .map(id -> currentQuantities.getOrDefault(id, 0))
                .collect(Collectors.toList());
    
            int numberOfPairs = 0;
            Map<String, Integer> consumedQuantities = new HashMap<>();
    
            if (type == PairingRule.PairingType.MIN) {
                // MIN类型：取所有子件中数量最小的值
                numberOfPairs = childQuantities.stream().mapToInt(Integer::intValue).min().orElse(0);
                // 每个子件消耗相同数量
                for (String childId : childIds) {
                    consumedQuantities.put(childId, numberOfPairs);
                }
            } else if (type == PairingRule.PairingType.MAX) {
                // MAX类型：只要有任何子件存在就可以形成配对
                if (childQuantities.stream().anyMatch(qty -> qty > 0)) {
                    // 取所有子件中数量最大的值
                    numberOfPairs = childQuantities.stream().mapToInt(Integer::intValue).max().orElse(0);
                    // 每个子件消耗其可用数量和配对数量中的较小值
                    for (int i = 0; i < childIds.size(); i++) {
                        consumedQuantities.put(childIds.get(i), 
                            Math.min(childQuantities.get(i), numberOfPairs));
                    }
                }
            }
    
            if (numberOfPairs > 0) {
                OrderItemDto pairItem = new OrderItemDto();
                pairItem.setProductId(pairId);
                pairItem.setQuantity(numberOfPairs);
                optimizedItems.add(pairItem);
    
                // 更新剩余数量
                consumedQuantities.forEach((childId, consumed) -> {
                    int remaining = currentQuantities.getOrDefault(childId, 0) - consumed;
                    currentQuantities.put(childId, remaining);
                });
            }
        }
    
        // Add remaining individual items
        currentQuantities.forEach((productId, quantity) -> {
            if (quantity > 0) {
                OrderItemDto remainingItem = new OrderItemDto();
                remainingItem.setProductId(productId);
                remainingItem.setQuantity(quantity);
                optimizedItems.add(remainingItem);
            }
        });
        
        // 确保没有重复的产品，如果有则合并数量
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
                .filter(item -> item.getQuantity() > 0)
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
