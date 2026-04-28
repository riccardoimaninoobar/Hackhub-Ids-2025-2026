package it.unicam.hackhub.presentation.dto;

import java.time.LocalDate;

public record HackathonResponse(
        String nome,
        String luogo,
        LocalDate inizio,
        LocalDate fine,
        LocalDate scadenzaIscrizioni, String organizzatore,
        String stato
) {}