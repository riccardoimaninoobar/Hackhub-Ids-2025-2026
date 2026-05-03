package it.unicam.hackhub.domain.model.eventi;

import it.unicam.hackhub.domain.model.EventoNotificabile;
import it.unicam.hackhub.domain.model.RichiestaSupporto;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.model.invito.Invito;
import org.springframework.context.ApplicationEvent;

public class RichiestaSupportoInviataEvent extends ApplicationEvent implements EventoNotificabile {

    private final RichiestaSupporto richiestaSupporto;
    private final Utente destinatario;

    public RichiestaSupportoInviataEvent(Object source, Utente destinario, RichiestaSupporto richiestaSupporto) {
        super(source);
        this.destinatario = destinario;
        this.richiestaSupporto = richiestaSupporto;
    }

    @Override
    public Utente getDestinatarioNotifica() {
        return destinatario;
    }

    @Override
    public String getTestoNotifica() {
        return "Il team " + richiestaSupporto.getTeam().getNome() + " ha inserito " +
                "una richiesta di supporto per l'hackathon " + richiestaSupporto.getHackathon().getNome();

    }
}