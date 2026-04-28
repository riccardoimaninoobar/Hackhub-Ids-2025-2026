package it.unicam.hackhub.presentation.dto;

// DTO per ricevere i dati dal client
public record SottomissioneRequest(
        Long hackathonId,
        String link
) {}