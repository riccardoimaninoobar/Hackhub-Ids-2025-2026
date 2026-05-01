package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.invito.Invito;
import it.unicam.hackhub.domain.model.Notifica;
import it.unicam.hackhub.domain.model.invito.state.StatoPendente;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.model.NotificaEvent;
import it.unicam.hackhub.domain.repository.InvitoRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GestioneInvitiHandler {

    private final UtenteRepository utenteRepo;
    private final InvitoRepository invitoRepo;
    private final Sessione sessione;
    private final ApplicationEventPublisher eventPublisher;

    public GestioneInvitiHandler(UtenteRepository utenteRepo, InvitoRepository invitoRepo, Sessione sessione, ApplicationEventPublisher eventPublisher) {
        this.utenteRepo = utenteRepo;
        this.invitoRepo = invitoRepo;
        this.sessione = sessione;
        this.eventPublisher = eventPublisher;
    }

    // EARLY EXIT per la CLI
    public void checkPrerequisiti() {
        Utente u = sessione.getUtenteCorrente();
        if (u == null) {
            throw new IllegalStateException("Devi effettuare il login per invitare qualcuno.");
        }
        if (u.getTeam() == null) {
            throw new IllegalStateException("Devi far parte di un team per invitare qualcuno.");
        }
    }

    public void elaboraInvito(String username) {
        checkPrerequisiti();
        Team teamCorrente = sessione.getUtenteCorrente().getTeam();

        Optional<Utente> optUtente = utenteRepo.findByUsername(username);
        if (optUtente.isEmpty()) {
            throw new IllegalArgumentException("Utente inesistente");
        }

        Utente invitato = optUtente.get();
        if (invitato.hasTeam()) {
            throw new IllegalArgumentException("L'utente è già in un team");
        }

        boolean giaInvitato = invitoRepo.existsByInvitatoAndTeamMittenteAndStato(invitato, teamCorrente, new StatoPendente());
        if (giaInvitato) {
            throw new IllegalArgumentException("L'utente è già stato invitato");
        }

        // Tutto ok, si procede con l'invito!
        Invito nuovoInvito = new Invito(invitato, teamCorrente);
        invitoRepo.save(nuovoInvito);

        Notifica notifica = new Notifica(invitato, "Nuovo Invito", "Sei stato invitato a unirti al team " + teamCorrente.getNome());
        eventPublisher.publishEvent(new NotificaEvent(notifica));
    }
}