package it.unicam.hackhub.domain.model.eventi;

import it.unicam.hackhub.domain.model.EventoNotificabile;
import it.unicam.hackhub.domain.model.SegnalazioneViolazione;
import it.unicam.hackhub.domain.model.Utente;
import org.springframework.context.ApplicationEvent;

public class ViolazioneGestitaEvent extends ApplicationEvent implements EventoNotificabile {

    private final SegnalazioneViolazione segnalazione;

    public ViolazioneGestitaEvent(Object source, SegnalazioneViolazione segnalazione) {
        super(source);
        this.segnalazione = segnalazione;
    }

    // --- Implementazione dei metodi dell'interfaccia ---

    @Override
    public Utente getDestinatarioNotifica() {
        return this.segnalazione.getMentore();
    }

    @Override
    public String getTestoNotifica() {
        return "La segnalazione num." + segnalazione.getId() + "per  l'hackathon " + this.segnalazione.getHackathon().getNome()
                + " relativa al team "+this.segnalazione.getTeam().getNome() + " è stata gestita";
    }
}