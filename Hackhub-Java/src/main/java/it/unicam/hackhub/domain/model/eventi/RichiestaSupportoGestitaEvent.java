package it.unicam.hackhub.domain.model.eventi;

import it.unicam.hackhub.domain.model.EventoNotificabile;
import it.unicam.hackhub.domain.model.RichiestaSupporto;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.model.invito.Invito;
import org.springframework.context.ApplicationEvent;

public class RichiestaSupportoGestitaEvent extends ApplicationEvent implements EventoNotificabile {

    private final RichiestaSupporto richiestaSupporto;
    private final Utente destinatario;

    public RichiestaSupportoGestitaEvent(Object source, Utente destinario, RichiestaSupporto richiestaSupporto) {
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
        // Il testo viene generato dinamicamente leggendo l'entità aggiornata!
        return "La tua richiesta di supporto per l'hackathon "
                + this.richiestaSupporto.getHackathon().getNome()
                + " è stata gestita dal mentore. Risposta: "
                + this.richiestaSupporto.getRisposta()
                + ". Slot prenotato per il "
                + this.richiestaSupporto.getDataCall()
                + " alle " + this.richiestaSupporto.getOraCall() + ".";
    }
}