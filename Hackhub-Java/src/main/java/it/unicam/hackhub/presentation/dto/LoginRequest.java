package it.unicam.hackhub.presentation.dto;

public record LoginRequest(
        String username,
        String password
) {}