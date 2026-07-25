package com.foodflow.service;

import com.foodflow.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {

    void notify(Long userId, String message);

    List<NotificationResponse> getForUser(Long userId);

    NotificationResponse markAsRead(Long id);
}
