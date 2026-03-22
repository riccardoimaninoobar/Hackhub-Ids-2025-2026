package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Hackathon;
import it.unicam.hackhub.domain.model.Sottomissione;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;

import java.util.Optional;

public class CaricaSottomissioneHandler {

    private final HackathonRepository hackathonRepo;
    private final Sessione sessione; // AGGIUNTA LA SESSIONE

    public CaricaSottomissioneHandler(HackathonRepository hackathonRepo, Sessione sessione) {
        this.hackathonRepo = hackathonRepo;
        this.sessione = sessione;
    }

    // verificaStato(nomeHackathon)
    public String verificaStato(String nomeHackathon) {
        Optional<Hackathon> opt = hackathonRepo.findById(nomeHackathon);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Hackathon inesistente");
        }
        return opt.get().getStato();
    }

    // MODIFICA: Rimosso il parametro "Team team"
    public void caricamentoSottomissione(String nomeHackathon, String nomeFile, String link) {

        // --- 1. Controllo di Sicurezza tramite Sessione ---
        Utente utenteCorrente = sessione.getUtenteCorrente();
        if (utenteCorrente == null) {
            throw new IllegalStateException("Devi effettuare il login per caricare una sottomissione.");
        }

        Team team = utenteCorrente.getTeam();
        if (team == null) {
            throw new IllegalStateException("Devi far parte di un team per caricare una sottomissione.");
        }

        // --- 2. Recupero Hackathon e controlli ---
        Hackathon h = hackathonRepo.findById(nomeHackathon)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon inesistente"));

        if (!"in corso".equalsIgnoreCase(h.getStato())) {
            throw new IllegalStateException("Non puoi più caricare una sottomissione, l'Hackathon non è in corso.");
        }

        // MODIFICA: Ora passiamo direttamente l'utenteCorrente per verificare l'iscrizione!
        // Molto più pulito rispetto a "team.getMembers().iterator().next()"
        if (!h.utentePartecipante(utenteCorrente)) {
            throw new IllegalStateException("Il tuo team non è iscritto a questo hackathon");
        }

        // --- 3. Creazione Sottomissione e salvataggio ---
        Sottomissione sottomissione = new Sottomissione(nomeFile, link, team);
        h.aggiungiSottomissione(sottomissione);

        hackathonRepo.save(h);
        System.out.println("Sottomissione '" + nomeFile + "' caricata con successo!");
    }
}