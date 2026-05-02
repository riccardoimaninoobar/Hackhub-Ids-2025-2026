package it.unicam.hackhub.presentation.dto;

import it.unicam.hackhub.domain.model.EsitoSegnalazione;

public record GestioneViolazioneRequest(
        EsitoSegnalazione esito, // Es. ACCOLTA, RESPINTA
        String motivazione
) {}