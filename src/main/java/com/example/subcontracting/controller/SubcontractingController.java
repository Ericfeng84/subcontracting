// src/main/java/com/example/subcontracting/controller/SubcontractingController.java
package com.example.subcontracting.controller;

import com.example.subcontracting.dto.MaterialRequirementDto;
import com.example.subcontracting.dto.OrderRequestDto;
import com.example.subcontracting.service.SubcontractingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

// =============================
// 接口文档
// =============================
// 1. 计算物料需求
//    - URL: POST /api/subcontracting/calculate-materials
//    - 请求体: OrderRequestDto
//      {
//        "items": [
//          { "productId": "P001", "quantity": 10 },
//          { "productId": "P002", "quantity": 5 }
//        ]
//      }
//    - 响应体: List<MaterialRequirementDto>
//      [
//        { "materialId": "M001", "materialName": "原材料A", "quantity": 20 },
//        { "materialId": "M002", "materialName": "原材料B", "quantity": 10 }
//      ]
//    - 错误响应: HTTP 400 Bad Request
//    - 用途：根据订单需求，返回所需原材料及数量。
// =============================

@RestController
@RequestMapping("/api/subcontracting")
public class SubcontractingController {

    private final SubcontractingService subcontractingService;

    @Autowired
    public SubcontractingController(SubcontractingService subcontractingService) {
        this.subcontractingService = subcontractingService;
    }

    @PostMapping("/calculate-materials")
    public ResponseEntity<List<MaterialRequirementDto>> calculateMaterials(@RequestBody OrderRequestDto orderRequest) {
        try {
            List<MaterialRequirementDto> requirements = subcontractingService.calculateRequiredMaterials(orderRequest);
            return ResponseEntity.ok(requirements);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Or a proper error DTO
        }
    }

    // 新增：获取所有成品产品列表，供前端下拉选择
    @GetMapping("/products")
    public List<Map<String, String>> getAllFinishedProducts() {
        // 只返回成品（FINISHED_GOOD）
        return subcontractingService.getAllFinishedProducts().stream()
                .map(p -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", p.getId());
                    map.put("name", p.getName());
                    return map;
                })
                .toList();
    }
}
