package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Invito;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.InvitoRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;

import java.util.Optional;

public class GestioneInvitiHandler {

    private final UtenteRepository utenteRepo;
    private final InvitoRepository invitoRepo;
    private final Sessione sessione;

    public GestioneInvitiHandler(UtenteRepository utenteRepo, InvitoRepository invitoRepo, Sessione sessione) {
        this.utenteRepo = utenteRepo;
        this.invitoRepo = invitoRepo;
        this.sessione = sessione;
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

        Optional<Utente> optUtente = utenteRepo.findById(username);
        if (optUtente.isEmpty()) {
            throw new IllegalArgumentException("Utente inesistente");
        }

        Utente invitato = optUtente.get();
        if (invitato.hasTeam()) {
            throw new IllegalArgumentException("L'utente è già in un team");
        }

        boolean giaInvitato = invitoRepo.existsActiveInvitation(invitato, teamCorrente, "IN_ATTESA");
        if (giaInvitato) {
            throw new IllegalArgumentException("L'utente è già stato invitato");
        }

        // Tutto ok, si procede con l'invito!
        Invito nuovoInvito = new Invito(invitato, teamCorrente);
        invitoRepo.save(nuovoInvito);
    }
}