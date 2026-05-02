package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.model.eventi.NotificaEvent;
import it.unicam.hackhub.domain.model.invito.Invito;
import it.unicam.hackhub.domain.model.invito.state.StatoPendente;
import it.unicam.hackhub.domain.repository.InvitoRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GestioneInvitiHandler {

    private final Sessione sessione;
    private final UtenteRepository utenteRepo;
    private final InvitoRepository invitoRepo;
    private final ApplicationEventPublisher eventPublisher;

    public GestioneInvitiHandler(Sessione sessione, UtenteRepository utenteRepo,
                                 InvitoRepository invitoRepo, ApplicationEventPublisher eventPublisher) {
        this.sessione = sessione;
        this.utenteRepo = utenteRepo;
        this.invitoRepo = invitoRepo;
        this.eventPublisher = eventPublisher;
    }

    public void checkPrerequisiti() {
        Utente utenteCorrente = sessione.getUtenteCorrente();
        if (utenteCorrente == null) {
            throw new IllegalStateException("Nessun utente autenticato in sessione.");
        }
        if (!utenteCorrente.hasTeam()) {
            throw new IllegalStateException("Devi far parte di un team per poter invitare altri utenti.");
        }
    }

    @Transactional
    public void elaboraInvito(String username) {
        checkPrerequisiti();
        Team teamMittente = sessione.getUtenteCorrente().getTeam();

        Utente invitato = utenteRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente inesistente"));

        if (invitato.hasTeam()) {
            throw new IllegalArgumentException("L'utente è già in un team");
        }

        if (invitoRepo.existsByInvitatoAndTeamMittenteAndStato(invitato, teamMittente, new StatoPendente())) {
            throw new IllegalArgumentException("Un invito per questo utente è già in attesa di risposta");
        }

        Invito nuovoInvito = new Invito(invitato, teamMittente);
        invitoRepo.save(nuovoInvito);

        eventPublisher.publishEvent(new NotificaEvent(
                invitato,
                "Nuovo Invito",
                "Sei stato invitato a unirti al team: " + teamMittente.getNome()
        ));
    }
}