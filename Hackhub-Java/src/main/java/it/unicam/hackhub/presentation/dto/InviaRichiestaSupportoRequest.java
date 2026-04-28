package it.unicam.hackhub.presentation.dto;

public record InviaRichiestaSupportoRequest(
        Long hackathonId,
        String descrizione
) {}