package it.unicam.hackhub.presentation.dto;

public record RegistrazioneRequest(
        String username,
        String email,
        String password
) {}