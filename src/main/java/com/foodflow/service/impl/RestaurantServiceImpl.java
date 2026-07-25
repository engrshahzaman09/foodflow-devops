package com.foodflow.service.impl;

import com.foodflow.dto.RestaurantRequest;
import com.foodflow.dto.RestaurantResponse;
import com.foodflow.entity.Restaurant;
import com.foodflow.exception.ResourceNotFoundException;
import com.foodflow.repository.RestaurantRepository;
import com.foodflow.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public RestaurantResponse create(RestaurantRequest request) {
        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .cuisineType(request.getCuisineType())
                .description(request.getDescription())
                .build();

        return toResponse(restaurantRepository.save(restaurant));
    }

    @Override
    public List<RestaurantResponse> getAll() {
        return restaurantRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public RestaurantResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    @Override
    public RestaurantResponse update(Long id, RestaurantRequest request) {
        Restaurant restaurant = findEntity(id);
        restaurant.setName(request.getName());
        restaurant.setAddress(request.getAddress());
        restaurant.setPhone(request.getPhone());
        restaurant.setCuisineType(request.getCuisineType());
        restaurant.setDescription(request.getDescription());

        return toResponse(restaurantRepository.save(restaurant));
    }

    @Override
    public void delete(Long id) {
        restaurantRepository.delete(findEntity(id));
    }

    private Restaurant findEntity(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
    }

    private RestaurantResponse toResponse(Restaurant restaurant) {
        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .phone(restaurant.getPhone())
                .cuisineType(restaurant.getCuisineType())
                .description(restaurant.getDescription())
                .createdAt(restaurant.getCreatedAt())
                .build();
    }
}
