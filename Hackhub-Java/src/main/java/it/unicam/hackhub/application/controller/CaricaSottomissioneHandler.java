package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.domain.model.Hackathon;
import it.unicam.hackhub.domain.model.Sottomissione;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.repository.HackathonRepository;

import java.util.Optional;

public class CaricaSottomissioneHandler {

    private final HackathonRepository hackathonRepo;

    public CaricaSottomissioneHandler(HackathonRepository hackathonRepo) {
        this.hackathonRepo = hackathonRepo;
    }

    // verificaStato(nomeHackathon)
    public String verificaStato(String nomeHackathon) {
        Optional<Hackathon> opt = hackathonRepo.findById(nomeHackathon);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Hackathon inesistente");
        }
        return opt.get().getStato();
    }

    // caricamentoSottomissione(nomeHackathon, nomeFile, link, team) - AGGIORNATO
    public void caricamentoSottomissione(String nomeHackathon, String nomeFile, String link, Team team) {
        Hackathon h = hackathonRepo.findById(nomeHackathon)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon inesistente"));

        if (!"in corso".equalsIgnoreCase(h.getStato())) {
            throw new IllegalStateException("Non puoi più caricare una sottomissione");
        }

        // verifica che il team sia iscritto (precondizione UC33)
        if (!h.utentePartecipante(team.getMembers().iterator().next())) {
            throw new IllegalStateException("Il tuo team non è iscritto a questo hackathon");
        }

        Sottomissione sottomissione = new Sottomissione(nomeFile, link, team);
        h.aggiungiSottomissione(sottomissione);

        hackathonRepo.save(h);
        System.out.println("Sottomissione '" + nomeFile + "' caricata con successo!");
    }
}