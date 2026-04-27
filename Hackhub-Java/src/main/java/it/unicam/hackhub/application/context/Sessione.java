package it.unicam.hackhub.application.context;

import it.unicam.hackhub.domain.model.Utente;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class Sessione {
    private Utente utenteCorrente;

    public Sessione() {
        this.utenteCorrente = null;
    }
    public Utente getUtenteCorrente() {
        return utenteCorrente;
    }
    public void setUtenteCorrente(Utente utenteCorrente) {
        this.utenteCorrente = utenteCorrente;
    }
}
