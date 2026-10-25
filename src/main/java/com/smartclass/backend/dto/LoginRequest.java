package com.smartclass.backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginRequest(
        @JsonProperty("username")
        @JsonAlias("email")
        String username,
        String password
) {
}
