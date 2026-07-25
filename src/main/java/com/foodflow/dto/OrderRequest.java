package com.foodflow.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long restaurantId;

    @NotEmpty
    @Valid
    private List<OrderItemRequest> items;
}
