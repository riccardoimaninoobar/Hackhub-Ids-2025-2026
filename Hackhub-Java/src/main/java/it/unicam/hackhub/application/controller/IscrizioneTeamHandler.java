package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Hackathon;
import it.unicam.hackhub.domain.model.StatoInIscrizione;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
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



    public Set<Hackathon> getHackathonInIscrizione() {
        // Corrisponde a findByStato("In iscrizione") sul repository
        return hackathonRepo.findAll().stream()
                .filter(h -> h.getStato() instanceof StatoInIscrizione)
                .collect(Collectors.toSet());
    }

    public void iscriviTeam(Hackathon h) {
        // 1. Recupero dati dalla sessione come nel diagramma
        Utente utenteCorrente = sessione.getUtenteCorrente();
        if (utenteCorrente == null) throw new IllegalStateException("Login richiesto.");

        Team team = utenteCorrente.getTeam();
        if (team == null) throw new IllegalStateException("Nessun team associato.");

        // 2. Chiamata all'Hackathon (che delega allo stato)
        h.iscriviTeam(team);

        // 3. Salvataggio su repository
        hackathonRepo.save(h);
    }
}