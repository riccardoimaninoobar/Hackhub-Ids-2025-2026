package it.unicam.hackhub.validation;

import java.util.regex.Pattern;

/**
 * Validatore per gli input dell'utente (username, email, ecc.).
 */
public class InputValidator {

    private static final int MIN_USERNAME_LENGTH = 3;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    /**
     * Valida uno username.
     * @param username lo username da validare
     * @return null se valido, altrimenti il messaggio di errore
     */
    public static String validateUsername(String username) {
        if (username == null || username.isBlank()) {
            return "Lo username è obbligatorio";
        }
        if (username.length() < MIN_USERNAME_LENGTH) {
            return "Lo username deve avere almeno " + MIN_USERNAME_LENGTH + " caratteri";
        }
        if (username.contains(" ")) {
            return "Lo username non può contenere spazi";
        }
        return null;
    }

    /**
     * Valida un'email.
     * @param email l'email da validare
     * @return null se valida, altrimenti il messaggio di errore
     */
    public static String validateEmail(String email) {
        if (email == null || email.isBlank()) {
            return "L'email è obbligatoria";
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "Formato email non valido";
        }
        return null;
    }

    /**
     * Valida un nome o cognome.
     * @param value il valore da validare
     * @param fieldName il nome del campo (per il messaggio di errore)
     * @return null se valido, altrimenti il messaggio di errore
     */
    public static String validateNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return fieldName + " è obbligatorio";
        }
        return null;
    }
}
