package com.foodflow.service;

import com.foodflow.dto.LoginRequest;
import com.foodflow.dto.LoginResponse;
import com.foodflow.dto.UserRequest;
import com.foodflow.dto.UserResponse;

public interface UserService {

    UserResponse register(UserRequest request);

    LoginResponse login(LoginRequest request);
}