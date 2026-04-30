package it.unicam.hackhub.presentation.dto;

public record SegnalazioneRequest(
        Long hackathonId,
        Long teamId,
        String descrizione
) {}