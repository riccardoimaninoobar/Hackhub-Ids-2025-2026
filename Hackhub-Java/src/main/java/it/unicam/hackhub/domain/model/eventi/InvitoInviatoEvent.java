package it.unicam.hackhub.domain.model.eventi;

import it.unicam.hackhub.domain.model.EventoNotificabile;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.model.invito.Invito;
import org.springframework.context.ApplicationEvent;

public class InvitoInviatoEvent extends ApplicationEvent implements EventoNotificabile {

    private final Invito invito;

    public InvitoInviatoEvent(Object source, Invito invito) {
        super(source);
        this.invito = invito;
    }

    @Override
    public Utente getDestinatarioNotifica() {
        return this.invito.getInvitato();
    }

    @Override
    public String getTestoNotifica() {
        return "Sei stato invitato a unirti al team: " + this.invito.getTeamMittente().getNome();
    }
}