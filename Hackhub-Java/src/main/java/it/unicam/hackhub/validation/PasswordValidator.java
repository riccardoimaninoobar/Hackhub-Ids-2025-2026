package it.unicam.hackhub.validation;

/**
 * Validatore per le password degli utenti.
 * Requisiti:
 * - Minimo 8 caratteri
 * - Almeno una lettera maiuscola
 * - Almeno una cifra
 */
public class PasswordValidator {

    private static final int MIN_LENGTH = 8;

    /**
     * Valida una password secondo i criteri di sicurezza.
     * @param password la password da validare
     * @return true se la password è valida
     */
    public static boolean isValid(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            return false;
        }

        boolean hasMaiuscola = password.chars().anyMatch(Character::isUpperCase);
        if (!hasMaiuscola) {
            return false;
        }

        boolean hasCifra = password.chars().anyMatch(Character::isDigit);
        if (!hasCifra) {
            return false;
        }

        return true;
    }

    /**
     * Restituisce un messaggio descrittivo dei requisiti della password.
     */
    public static String getRequirements() {
        return "La password deve contenere almeno " + MIN_LENGTH + " caratteri, una lettera maiuscola e una cifra";
    }

    /**
     * Valida la password e restituisce un messaggio di errore specifico.
     * @param password la password da validare
     * @return null se valida, altrimenti il messaggio di errore
     */
    public static String validate(String password) {
        if (password == null || password.isEmpty()) {
            return "La password è obbligatoria";
        }
        if (password.length() < MIN_LENGTH) {
            return "La password deve avere almeno " + MIN_LENGTH + " caratteri";
        }
        if (password.chars().noneMatch(Character::isUpperCase)) {
            return "La password deve contenere almeno una lettera maiuscola";
        }
        if (password.chars().noneMatch(Character::isDigit)) {
            return "La password deve contenere almeno una cifra";
        }
        return null;
    }
}
