package it.unicam.hackhub.domain.model.eventi;

import it.unicam.hackhub.domain.model.EventoNotificabile;
import it.unicam.hackhub.domain.model.Utente;
import org.springframework.context.ApplicationEvent;

public class ViolazioneGestitaEvent extends ApplicationEvent implements EventoNotificabile {

    private final String nomeHackathon;
    private final Utente mentore; // Il destinatario

    public ViolazioneGestitaEvent(Object source, String nomeHackathon, Utente mentore) {
        super(source);
        this.nomeHackathon = nomeHackathon;
        this.mentore = mentore;
    }

    // --- Implementazione dei metodi dell'interfaccia ---

    @Override
    public Utente getDestinatarioNotifica() {
        return this.mentore;
    }

    @Override
    public String getTestoNotifica() {
        return "Attenzione! È stata inserita una nuova violazione per l'hackathon: " + this.nomeHackathon;
    }
}