package com.foodflow.service.impl;

import com.foodflow.dto.NotificationResponse;
import com.foodflow.entity.Notification;
import com.foodflow.exception.ResourceNotFoundException;
import com.foodflow.repository.NotificationRepository;
import com.foodflow.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * In-app notification implementation: stores notifications in the database
 * so they can be fetched via API (e.g. for a notifications bell/inbox in the frontend).
 *
 * NOTE: This does NOT send real emails yet. To add email delivery, inject
 * Spring's JavaMailSender here and call it alongside the DB save below —
 * that requires SMTP credentials (host/port/username/app-password) in
 * application.properties, which are not configured in this project yet.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void notify(Long userId, String message) {
        Notification notification = Notification.builder()
                .userId(userId)
                .message(message)
                .isRead(false)
                .build();

        notificationRepository.save(notification);

        log.info("Notification created for user {}: {}", userId, message);
        // TODO: hook real email sending here once SMTP is configured.
    }

    @Override
    public List<NotificationResponse> getForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public NotificationResponse markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        notification.setRead(true);
        return toResponse(notificationRepository.save(notification));
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .message(notification.getMessage())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
