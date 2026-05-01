package it.unicam.hackhub.presentation.dto;

import it.unicam.hackhub.domain.model.StatoSegnalazione;

public record GestioneViolazioneRequest(
        StatoSegnalazione esito, // Es. ACCOLTA, RESPINTA
        String motivazione
) {}