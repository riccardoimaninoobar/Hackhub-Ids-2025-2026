package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Hackathon;
import it.unicam.hackhub.domain.model.Sottomissione;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import java.util.Set;

public class CaricaSottomissioneHandler {

    private final HackathonRepository hackathonRepo;
    private final Sessione sessione;

    public CaricaSottomissioneHandler(HackathonRepository hackathonRepo, Sessione sessione) {
        this.hackathonRepo = hackathonRepo;
        this.sessione = sessione;
    }

    /**
     * Recupera gli hackathon filtrati per lo stato "In corso" tramite il Team.
     */
    public Set<Hackathon> getHackathonInCorso() {
        Utente corrente = sessione.getUtenteCorrente();
        if (corrente == null) throw new IllegalStateException("Devi effettuare il login.");

        Team t = corrente.getTeam();
        if (t == null) throw new IllegalStateException("Devi far parte di un team.");

        // Chiamata delegata al Team come da diagramma
        return t.getHackathonInCorso();
    }

    /**
     * Esegue la creazione della sottomissione e la delega allo stato dell'hackathon.
     */
    public void caricaSottomissione(Hackathon h, String link) {
        // 1. Recupero del Team dalla sessione
        Team team = sessione.getUtenteCorrente().getTeam();

        // 2. Creazione della sottomissione (<<create>> nel diagramma)
        Sottomissione s = new Sottomissione("Sottomissione_" + h.getNome(), link, team);

        // 3. Delega all'Hackathon (il quale delegherà allo Stato)
        // Se lo stato != "In corso", l'oggetto Stato lancerà OperazioneNonConsentita
        h.caricaSottomissione(s);

        // 4. Persistenza
        hackathonRepo.save(h);
    }
}