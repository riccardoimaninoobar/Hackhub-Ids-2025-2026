package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.TeamRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository; // AGGIUNTO
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // AGGIUNTO

@Service
public class CreazioneTeamHandler {

    private final TeamRepository teamRepository;
    private final Sessione sessione;
    private final UtenteRepository utenteRepository; // AGGIUNTO

    // Aggiorna il costruttore
    public CreazioneTeamHandler(TeamRepository teamRepository, Sessione sessione, UtenteRepository utenteRepository) {
        this.teamRepository = teamRepository;
        this.sessione = sessione;
        this.utenteRepository = utenteRepository;
    }

    public boolean verificaTeamEsistente(String nomeTeam) {
        return teamRepository.existsByNome(nomeTeam);
    }

    public boolean verificaUtenteInTeam(Utente u) {
        return u.getTeam() != null;
    }

    @Transactional
    public Team creaTeam(String nomeTeam, String datiBancari) {
        Utente utenteInSessione = sessione.getUtenteCorrente();
        if (utenteInSessione == null) {
            throw new IllegalStateException("Devi effettuare il login per creare un team.");
        }

        // Recuperiamo l'utente connesso al DB
        Utente u = utenteRepository.findById(utenteInSessione.getId())
                .orElseThrow(() -> new IllegalStateException("Utente non più valido nel DB."));

        if (verificaUtenteInTeam(u)) {
            throw new IllegalStateException("L'utente è già in un team.");
        }
        if (verificaTeamEsistente(nomeTeam)) {
            throw new IllegalArgumentException("Esiste già un team con questo nome.");
        }

        Team newTeam = new Team(nomeTeam);
        newTeam.setDatiBancari(datiBancari);

        newTeam.aggiungiMembro(u);
        teamRepository.save(newTeam);

        sessione.setUtenteCorrente(u);

        return newTeam;
    }
}