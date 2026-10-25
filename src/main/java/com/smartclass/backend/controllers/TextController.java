package com.smartclass.backend.controllers;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/texts")
public class TextController {


    @GetMapping
    public ResponseEntity<?> getSecretText(
            @AuthenticationPrincipal String email
    ) {

        final var response =
                "Currently logged in as %s"
                        .formatted(email);


        return ResponseEntity.ok(response);
    }
}