package it.unicam.hackhub.presentation.dto;
import java.time.LocalDateTime;
public record SottomissioneResponse(
        Long id,
        String nomeFile,
        String link,
        LocalDateTime dataCaricamento,
        String nomeTeam,
        int punteggio
) {}
