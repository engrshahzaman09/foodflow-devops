package com.foodflow.service.impl;

import com.foodflow.dto.PaymentRequest;
import com.foodflow.dto.PaymentResponse;
import com.foodflow.entity.Order;
import com.foodflow.entity.OrderStatus;
import com.foodflow.entity.Payment;
import com.foodflow.entity.PaymentStatus;
import com.foodflow.exception.DuplicateResourceException;
import com.foodflow.exception.ResourceNotFoundException;
import com.foodflow.repository.OrderRepository;
import com.foodflow.repository.PaymentRepository;
import com.foodflow.service.NotificationService;
import com.foodflow.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Dummy payment gateway simulation.
 * NOTE: This does not integrate with a real payment provider (e.g. Stripe/PayPal).
 * It simulates a successful transaction and generates a mock transaction ID,
 * which is sufficient for portfolio/demo purposes.
 */
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {

        if (paymentRepository.findByOrderId(request.getOrderId()).isPresent()) {
            throw new DuplicateResourceException("Payment already exists for order id: " + request.getOrderId());
        }

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.getOrderId()));

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .paymentMethod(request.getPaymentMethod())
                .transactionId("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase())
                .status(PaymentStatus.SUCCESS) // dummy gateway: always succeeds
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        notificationService.notify(order.getUser().getId(),
                "Payment of " + savedPayment.getAmount() + " received for order #" + order.getId() + ".");

        return toResponse(savedPayment);
    }

    @Override
    public PaymentResponse getByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No payment found for order id: " + orderId));

        return toResponse(payment);
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .transactionId(payment.getTransactionId())
                .status(payment.getStatus().name())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
