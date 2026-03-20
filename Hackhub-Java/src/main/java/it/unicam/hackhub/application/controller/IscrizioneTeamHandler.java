package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.domain.model.Hackathon;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.TeamRepository;

import java.util.Optional;

public class IscrizioneTeamHandler {

    private final HackathonRepository hackathonRepo;
    private final TeamRepository teamRepo;

    public IscrizioneTeamHandler(HackathonRepository hackathonRepo,
                                 TeamRepository teamRepo) {
        this.hackathonRepo = hackathonRepo;
        this.teamRepo = teamRepo;
    }

    // usato da IscrizioneTeamCLI per verificare lo stato dell'hackathon
    public String verificaStato(String nomeHackathon) {
        Optional<Hackathon> opt = hackathonRepo.findById(nomeHackathon);
        if (opt.isEmpty()) {
            // modelliamo il messaggio "Hackathon inesistente" con eccezione
            throw new IllegalArgumentException("Hackathon inesistente");
        }
        Hackathon h = opt.get();
        return h.getStato();
    }

    // flusso principale di iscrizione: iscriviTeamHandler(nomeHackathon, teamId)
    public void iscriviTeamHandler(String nomeHackathon, String teamId) {
        Hackathon h = hackathonRepo.findById(nomeHackathon)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon inesistente"));

        // controllo sullo stato, come nel blocco alt stato == inIscrizione
        if (!"In iscrizione".equalsIgnoreCase(h.getStato())) {
            // Eccezione("Non puoi iscriverti a questo Hackathon")
            throw new IllegalStateException("Non puoi iscriverti a questo Hackathon");
        }

        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team inesistente"));

        // aggiungiTeam(team) sul dominio
        h.aggiungiTeam(team);

        // save(h) sul repository
        hackathonRepo.save(h);
        // "iscrizione completata(success)" è rappresentato dal fatto che non viene lanciata eccezione
    }
}