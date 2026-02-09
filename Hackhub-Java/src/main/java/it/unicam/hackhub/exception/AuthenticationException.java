package it.unicam.hackhub.exception;

/**
 * Eccezione per errori di autenticazione (login fallito).
 */
public class AuthenticationException extends HackHubException {

    public AuthenticationException(String message) {
        super(message);
    }
}
