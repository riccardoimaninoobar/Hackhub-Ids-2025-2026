package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.hackathon.Hackathon;
import it.unicam.hackhub.domain.model.hackathon.state.StatoInIscrizione;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // AGGIUNTO

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class IscrizioneTeamHandler {

    private final HackathonRepository hackathonRepo;
    private final TeamRepository teamRepo;
    private final Sessione sessione;

    public IscrizioneTeamHandler(HackathonRepository hackathonRepo,
                                 TeamRepository teamRepo,
                                 Sessione sessione) {
        this.hackathonRepo = hackathonRepo;
        this.teamRepo = teamRepo;
        this.sessione = sessione;
    }

    public Set<Hackathon> getHackathonInIscrizione() {
        return hackathonRepo.findAll().stream()
                .filter(h -> h.getStato() instanceof StatoInIscrizione)
                .collect(Collectors.toSet());
    }

    @Transactional
    public void iscriviTeam(String nomeHackathon) {
        Utente utenteCorrente = sessione.getUtenteCorrente();
        if (utenteCorrente == null) throw new IllegalStateException("Devi effettuare il login per iscriverti.");

        Team teamDallaSessione = utenteCorrente.getTeam();
        if (teamDallaSessione == null) throw new IllegalStateException("Nessun team associato. Devi prima creare o unirti a un team.");

        Team teamConnesso = teamRepo.findById(teamDallaSessione.getId())
                .orElseThrow(() -> new IllegalStateException("Team non trovato nel database."));

        Hackathon hackathon = hackathonRepo.findByNome(nomeHackathon)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon '" + nomeHackathon + "' inesistente."));

        hackathon.iscriviTeam(teamConnesso);
        hackathonRepo.save(hackathon);
    }

    // PER CLI
    @Transactional
    public void iscriviTeam(Hackathon h) {
        iscriviTeam(h.getNome());
    }
}