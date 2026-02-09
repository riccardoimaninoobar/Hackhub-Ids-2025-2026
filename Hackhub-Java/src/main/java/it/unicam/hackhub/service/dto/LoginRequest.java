package it.unicam.hackhub.service.dto;

/**
 * DTO per la richiesta di login.
 */
public record LoginRequest(
    String username,
    String password
) {
    public LoginRequest {
        username = username != null ? username.trim() : "";
        password = password != null ? password : "";
    }
}
