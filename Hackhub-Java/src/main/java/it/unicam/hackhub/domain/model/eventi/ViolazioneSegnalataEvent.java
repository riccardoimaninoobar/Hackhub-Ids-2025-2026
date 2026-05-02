package it.unicam.hackhub.domain.model.eventi;

import it.unicam.hackhub.domain.model.EventoNotificabile;
import it.unicam.hackhub.domain.model.Utente;
import org.springframework.context.ApplicationEvent;

public class ViolazioneSegnalataEvent extends ApplicationEvent implements EventoNotificabile {

    private final String nomeHackathon;
    private final Utente organizzatore; // Il destinatario

    public ViolazioneSegnalataEvent(Object source, String nomeHackathon, Utente organizzatore) {
        super(source);
        this.nomeHackathon = nomeHackathon;
        this.organizzatore = organizzatore;
    }

    // --- Implementazione dei metodi dell'interfaccia ---

    @Override
    public Utente getDestinatarioNotifica() {
        return this.organizzatore;
    }

    @Override
    public String getTestoNotifica() {
        return "Attenzione! È stata inserita una nuova violazione per l'hackathon: " + this.nomeHackathon;
    }
}