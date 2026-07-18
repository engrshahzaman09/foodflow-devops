package com.foodflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LoginResponse {

    private String token;

    private String tokenType;

    private Long id;

    private String fullName;

    private String email;

    private String role;
}
