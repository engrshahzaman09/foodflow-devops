package com.foodflow.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {

    private Long menuItemId;
    private String menuItemName;
    private int quantity;
    private BigDecimal priceAtOrder;
}
