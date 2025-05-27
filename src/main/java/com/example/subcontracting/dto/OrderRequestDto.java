package com.example.subcontracting.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequestDto {
    private List<OrderItemDto> items;
}
