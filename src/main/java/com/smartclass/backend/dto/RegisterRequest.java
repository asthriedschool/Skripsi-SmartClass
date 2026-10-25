package com.smartclass.backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterRequest(
        @JsonProperty("full_name")
        @JsonAlias("fullName")
        String fullName,
        String email,
        String password
) {
}
