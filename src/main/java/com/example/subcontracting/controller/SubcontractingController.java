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

import java.util.List;

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
}
