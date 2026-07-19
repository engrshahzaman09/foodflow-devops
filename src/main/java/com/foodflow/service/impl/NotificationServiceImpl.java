package com.foodflow.service.impl;

import com.foodflow.dto.NotificationResponse;
import com.foodflow.entity.Notification;
import com.foodflow.entity.User;
import com.foodflow.exception.ResourceNotFoundException;
import com.foodflow.repository.NotificationRepository;
import com.foodflow.repository.UserRepository;
import com.foodflow.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Value("${notification.email.enabled:false}")
    private boolean emailEnabled;

    @Value("${notification.email.from:noreply@foodflow.com}")
    private String fromAddress;

    @Override
    public void notify(Long userId, String message) {
        Notification notification = Notification.builder()
                .userId(userId)
                .message(message)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        log.info("Notification created for user {}: {}", userId, message);

        if (emailEnabled) {
            sendEmailSafely(userId, message);
        }
    }

    private void sendEmailSafely(Long userId, String message) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null || user.getEmail() == null) {
                log.warn("Skipping email notification: no email found for user {}", userId);
                return;
            }

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromAddress);
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("FoodFlow Notification");
            mailMessage.setText(message);

            mailSender.send(mailMessage);
            log.info("Email notification sent to {}", user.getEmail());
        } catch (Exception ex) {
            log.error("Failed to send email notification for user {}: {}", userId, ex.getMessage());
        }
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
