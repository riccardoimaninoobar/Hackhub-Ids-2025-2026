package it.unicam.hackhub.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreazioneHackathonRequest(
        String nome,
        String regolamento,
        LocalDate scadenza,
        LocalDate inizio,
        LocalDate fine,
        String luogo,
        Integer maxTeam,
        BigDecimal premio,
        String nomeGiudice
) {}