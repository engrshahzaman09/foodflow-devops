package com.foodflow.service;

import com.foodflow.dto.PaymentRequest;
import com.foodflow.dto.PaymentResponse;

public interface PaymentService {

    PaymentResponse processPayment(PaymentRequest request);

    PaymentResponse getByOrderId(Long orderId);
}
