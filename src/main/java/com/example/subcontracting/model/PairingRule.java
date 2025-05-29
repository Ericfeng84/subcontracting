// src/main/java/com/example/subcontracting/model/PairingRule.java
package com.example.subcontracting.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class PairingRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pairProductId;
    
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> childProductIds = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private PairingType type;

    public enum PairingType {
        MIN, // 取所有子件中数量最小的值
        MAX  // 取所有子件中数量最大的值
    }

    // 构造函数支持多个子件
    public PairingRule(Long id, String pairProductId, List<String> childProductIds, PairingType type) {
        this.id = id;
        this.pairProductId = pairProductId;
        this.childProductIds = childProductIds;
        this.type = type;
    }

    // JPA需要的默认构造函数
    public PairingRule() {
    }

    // 获取所有子件ID的集合
    public Set<String> getChildProductIds() {
        return new HashSet<>(childProductIds);
    }
}
