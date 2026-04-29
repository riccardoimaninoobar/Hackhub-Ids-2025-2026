package it.unicam.hackhub.presentation.dto;
public record ValutazioneRequest(
        String nomeHackathon,
        Long idSottomissione,
        int punteggio
) {}
