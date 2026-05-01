package it.unicam.hackhub.presentation.dto;

public record SegnalazioneResponse(
        Long id,
        String nomeHackathon,
        String nomeTeam,
        String descrizione,
        String stato
) {}