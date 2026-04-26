package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import org.springframework.stereotype.Service;

@Service
public class LogoutHandler {
    private final Sessione sessione;

    public LogoutHandler(Sessione sessione) {
        this.sessione = sessione;
    }

    // Metodo preventivo per far controllare alla CLI se può procedere
    public void verificaSessione() {
        if (sessione.getUtenteCorrente() == null) {
            throw new IllegalStateException("Nessun utente attualmente loggato. Impossibile effettuare il logout.");
        }
    }

    public void effettuaLogout() {
        sessione.setUtenteCorrente(null);
    }
}