package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.domain.model.Hackathon;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;

import java.util.Optional;

public class AggiungiMentoreHandler {
    private final HackathonRepository hackathonRepo;
    private final UtenteRepository utenteRepo;
    private Hackathon hackathon;

    public AggiungiMentoreHandler(HackathonRepository hackathonRepo, UtenteRepository utenteRepo) {
        this.hackathonRepo = hackathonRepo;
        this.utenteRepo = utenteRepo;
    }
    public void checkOrg(Utente utente, String nomeHackathon) {
        Optional<Hackathon> h = hackathonRepo.findById(nomeHackathon);
        if(h.isEmpty()) { throw new IllegalArgumentException("Hackathon inesistente"); }
        Hackathon hackathon = h.get();
        if(!hackathon.isOrganizzatore(utente)) { throw new IllegalArgumentException("Utente non è Organizzatore dell'Hackathon"); }
        this.hackathon = hackathon;
    }
    public void aggiungiMentore(String username) {
        Optional<Utente> utente = utenteRepo.findById(username);
        if(utente.isEmpty()){ throw new IllegalArgumentException("Utente inesistente"); }
        if(hackathon.utenteMembroStaff(utente.get())){ throw new IllegalArgumentException("Utente già parte dello staff");}
        if(hackathon.utentePartecipante(utente.get())){throw new IllegalArgumentException("Utente partecipante");}
        hackathon.aggiungiMentore(utente.get());
        hackathonRepo.save(hackathon);
    }
}
