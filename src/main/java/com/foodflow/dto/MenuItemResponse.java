package com.foodflow.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MenuItemResponse {

    private Long id;
    private Long restaurantId;
    private String restaurantName;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private boolean available;
}
