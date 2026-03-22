package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Hackathon;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.TeamRepository;

import java.util.Optional;

public class IscrizioneTeamHandler {

    private final HackathonRepository hackathonRepo;
    private final TeamRepository teamRepo;
    private final Sessione sessione; // AGGIUNTA

    public IscrizioneTeamHandler(HackathonRepository hackathonRepo,
                                 TeamRepository teamRepo,
                                 Sessione sessione) {
        this.hackathonRepo = hackathonRepo;
        this.teamRepo = teamRepo;
        this.sessione = sessione;
    }

    public String verificaStato(String nomeHackathon) {
        Optional<Hackathon> opt = hackathonRepo.findById(nomeHackathon);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Hackathon inesistente");
        }
        Hackathon h = opt.get();
        return h.getStato();
    }

    // RIMOSSO: il parametro "String teamId"
    public void iscriviTeamHandler(String nomeHackathon) {
        // 1. Controllo di sicurezza tramite Sessione
        Utente utenteCorrente = sessione.getUtenteCorrente();
        if (utenteCorrente == null) {
            throw new IllegalStateException("Devi effettuare il login per iscrivere il team.");
        }

        Team team = utenteCorrente.getTeam();
        if (team == null) {
            throw new IllegalStateException("Non fai parte di nessun team, non puoi iscriverti.");
        }

        // 2. Recupero Hackathon
        Hackathon h = hackathonRepo.findById(nomeHackathon)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon inesistente"));

        if (!"In iscrizione".equalsIgnoreCase(h.getStato())) {
            throw new IllegalStateException("Non puoi iscriverti a questo Hackathon");
        }

        // 3. Iscrizione effettiva e salvataggio
        h.aggiungiTeam(team);
        hackathonRepo.save(h);
    }
}