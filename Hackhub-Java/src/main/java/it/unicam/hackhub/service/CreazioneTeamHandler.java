package it.unicam.hackhub.service;

import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.User;

import java.util.HashSet;
import java.util.Set;

public class CreazioneTeamHandler {

    private Set<Team> teams = new HashSet<>();

    // +CreazioneTeamHandler()
    public CreazioneTeamHandler() {
    }

    // +verificaTeamEsistente(nomeTeam : String)
    public boolean verificaTeamEsistente(String nomeTeam) {
        return teams.stream().anyMatch(team -> team.getName().equalsIgnoreCase(nomeTeam));
    }

    // +verificaUtenteInTeam(u : Utente)
    public boolean verificaUtenteInTeam(User u) {
        return u.getTeam() != null;
    }

    // +creaTeam(nomeTeam : String, u : Utente)
    public Team creaTeam(String nomeTeam, User u) {
        if (verificaUtenteInTeam(u)) {
            throw new IllegalStateException("L'utente è già in un team.");
        }
        if (verificaTeamEsistente(nomeTeam)) {
            throw new IllegalArgumentException("Esiste già un team con questo nome.");
        }

        Team newTeam = new Team(nomeTeam);
        newTeam.addMember(u);
        teams.add(newTeam);
        return newTeam;
    }
}