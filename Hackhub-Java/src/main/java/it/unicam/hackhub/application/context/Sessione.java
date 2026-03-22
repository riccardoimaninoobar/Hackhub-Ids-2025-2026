package it.unicam.hackhub.application.context;

import it.unicam.hackhub.domain.model.Utente;

public class Sessione {
    private Utente utenteCorrente;

    public Sessione(Utente utenteCorrente) {
        this.utenteCorrente = utenteCorrente;
    }
    public Utente getUtenteCorrente() {
        return utenteCorrente;
    }
    public void setUtenteCorrente(Utente utenteCorrente) {
        this.utenteCorrente = utenteCorrente;
    }
}
