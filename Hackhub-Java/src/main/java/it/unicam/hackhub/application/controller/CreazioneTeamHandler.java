package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.TeamRepository;

public class CreazioneTeamHandler { 

    private final TeamRepository teamRepository;

    // +CreazioneTeamHandler()
    public CreazioneTeamHandler(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    // +verificaTeamEsistente(nomeTeam : String)
    public boolean verificaTeamEsistente(String nomeTeam) {
        return teamRepository.existsById(nomeTeam);
    }

    // +verificaUtenteInTeam(u : Utente)
    public boolean verificaUtenteInTeam(Utente u) {
        return u.getTeam() != null;
    }

    // +creaTeam(nomeTeam : String, u : Utente)
    public Team creaTeam(String nomeTeam, Utente u) {
        if (verificaUtenteInTeam(u)) {
            throw new IllegalStateException("L'utente è già in un team.");
        }
        if (verificaTeamEsistente(nomeTeam)) {
            throw new IllegalArgumentException("Esiste già un team con questo nome.");
        }

        Team newTeam = new Team(nomeTeam);
        newTeam.addMember(u);
        teamRepository.save(newTeam);
        return newTeam;
    }
}