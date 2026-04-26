package it.unicam.hackhub.application.context;

import it.unicam.hackhub.domain.model.Utente;
import org.springframework.stereotype.Component;

@Component
public class Sessione {
    private Utente utenteCorrente;

    public Sessione() { }
    public Utente getUtenteCorrente() {
        return utenteCorrente;
    }
    public void setUtenteCorrente(Utente utenteCorrente) {
        this.utenteCorrente = utenteCorrente;
    }
}
