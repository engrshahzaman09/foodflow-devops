package com.foodflow.service;

import com.foodflow.dto.MenuItemRequest;
import com.foodflow.dto.MenuItemResponse;

import java.util.List;

public interface MenuItemService {

    MenuItemResponse create(MenuItemRequest request);

    List<MenuItemResponse> getByRestaurant(Long restaurantId);

    MenuItemResponse update(Long id, MenuItemRequest request);

    void delete(Long id);

    List<MenuItemResponse> search(String keyword);
}
