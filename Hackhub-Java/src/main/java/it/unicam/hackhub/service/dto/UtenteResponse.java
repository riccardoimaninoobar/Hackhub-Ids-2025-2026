package it.unicam.hackhub.service.dto;

import it.unicam.hackhub.domain.model.Utente;

import java.time.LocalDateTime;

/**
 * DTO per la risposta contenente i dati dell'utente (senza password).
 */
public record UtenteResponse(
    Long id,
    String username,
    String email,
    String nome,
    String cognome,
    LocalDateTime dataRegistrazione,
    Long teamId,
    String teamNome
) {
    /**
     * Crea un UtenteResponse a partire da un'entità Utente.
     */
    public static UtenteResponse fromEntity(Utente utente) {
        return new UtenteResponse(
            utente.getId(),
            utente.getUsername(),
            utente.getEmail(),
            utente.getNome(),
            utente.getCognome(),
            utente.getDataRegistrazione(),
            utente.getTeam() != null ? utente.getTeam().getId() : null,
            utente.getTeam() != null ? utente.getTeam().getNome() : null
        );
    }
}
