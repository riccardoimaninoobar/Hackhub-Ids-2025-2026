package it.unicam.hackhub.presentation.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record GestisciRichiestaRequest(
        String risposta,
        LocalDate data,
        LocalTime ora
) {}