package it.unicam.hackhub.exception;

/**
 * Eccezione per errori di validazione degli input.
 */
public class ValidationException extends HackHubException {

    public ValidationException(String message) {
        super(message);
    }
}
