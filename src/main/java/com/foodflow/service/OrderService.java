package com.foodflow.service;

import com.foodflow.dto.OrderRequest;
import com.foodflow.dto.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(OrderRequest request);

    OrderResponse getById(Long id);

    List<OrderResponse> getByUser(Long userId);

    OrderResponse cancelOrder(Long id);

    OrderResponse updateStatus(Long id, String status);
}
