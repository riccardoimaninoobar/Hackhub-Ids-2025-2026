package it.unicam.hackhub.exception;

/**
 * Eccezione generica per errori applicativi di HackHub.
 */
public class HackHubException extends RuntimeException {

    public HackHubException(String message) {
        super(message);
    }

    public HackHubException(String message, Throwable cause) {
        super(message, cause);
    }
}
