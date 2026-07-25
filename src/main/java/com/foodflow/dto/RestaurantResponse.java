package com.foodflow.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RestaurantResponse {

    private Long id;
    private String name;
    private String address;
    private String phone;
    private String cuisineType;
    private String description;
    private LocalDateTime createdAt;
}
