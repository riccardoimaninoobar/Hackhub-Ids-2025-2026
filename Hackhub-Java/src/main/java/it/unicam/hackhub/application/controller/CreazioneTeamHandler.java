package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.TeamRepository;
import org.springframework.stereotype.Service;

@Service
public class CreazioneTeamHandler {

    private final TeamRepository teamRepository;
    private final Sessione sessione; // AGGIUNTA

    public CreazioneTeamHandler(TeamRepository teamRepository, Sessione sessione) {
        this.teamRepository = teamRepository;
        this.sessione = sessione;
    }

    public boolean verificaTeamEsistente(String nomeTeam) {
        return teamRepository.existsById(nomeTeam);
    }

    public boolean verificaUtenteInTeam(Utente u) {
        return u.getTeam() != null;
    }

    // NON RICEVE PIU' L'UTENTE COME PARAMETRO DALLA CLI
    public Team creaTeam(String nomeTeam) {
        // --- LOGICA DI SESSIONE: Chi sta creando il team? ---
        Utente u = sessione.getUtenteCorrente();
        if (u == null) {
            throw new IllegalStateException("Devi effettuare il login per creare un team.");
        }

        if (verificaUtenteInTeam(u)) {
            throw new IllegalStateException("L'utente è già in un team.");
        }
        if (verificaTeamEsistente(nomeTeam)) {
            throw new IllegalArgumentException("Esiste già un team con questo nome.");
        }

        Team newTeam = new Team(nomeTeam);
        newTeam.aggiungiMembro(u); // Aggiunge l'utente loggato come creatore/membro
        teamRepository.save(newTeam);
        return newTeam;
    }
}