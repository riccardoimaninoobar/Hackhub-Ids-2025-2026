package it.unicam.hackhub.service.dto;

/**
 * DTO per la richiesta di registrazione di un nuovo utente.
 */
public record RegistrazioneRequest(
    String username,
    String email,
    String password,
    String nome,
    String cognome
) {
    /**
     * Costruttore con validazione base dei null.
     */
    public RegistrazioneRequest {
        username = username != null ? username.trim() : "";
        email = email != null ? email.trim().toLowerCase() : "";
        password = password != null ? password : "";
        nome = nome != null ? nome.trim() : "";
        cognome = cognome != null ? cognome.trim() : "";
    }
}
