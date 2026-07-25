package com.foodflow.service;

import com.foodflow.dto.RestaurantRequest;
import com.foodflow.dto.RestaurantResponse;

import java.util.List;

public interface RestaurantService {

    RestaurantResponse create(RestaurantRequest request);

    List<RestaurantResponse> getAll();

    RestaurantResponse getById(Long id);

    RestaurantResponse update(Long id, RestaurantRequest request);

    void delete(Long id);
}
