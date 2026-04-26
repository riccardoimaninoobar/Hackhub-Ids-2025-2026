package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Hackathon;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AggiungiMentoreHandler {
    private final HackathonRepository hackathonRepo;
    private final UtenteRepository utenteRepo;
    private final Sessione sessione; // AGGIUNTA
    private Hackathon hackathon;

    public AggiungiMentoreHandler(HackathonRepository hackathonRepo, UtenteRepository utenteRepo, Sessione sessione) {
        this.hackathonRepo = hackathonRepo;
        this.utenteRepo = utenteRepo;
        this.sessione = sessione;
    }

    // NON RICEVE PIU' L'UTENTE DALLA CLI
    public void checkOrg(String nomeHackathon) {
        // --- LOGICA DI SESSIONE ---
        Utente utente = sessione.getUtenteCorrente();
        if (utente == null) {
            throw new IllegalStateException("Devi effettuare il login per eseguire questa azione.");
        }

        Optional<Hackathon> h = hackathonRepo.findByNome(nomeHackathon);
        if(h.isEmpty()) {
            throw new IllegalArgumentException("Hackathon inesistente");
        }

        Hackathon hackathon = h.get();
        if(!hackathon.isOrganizzatore(utente)) {
            throw new IllegalArgumentException("L'utente loggato non è Organizzatore dell'Hackathon");
        }

        this.hackathon = hackathon;
    }

    public void aggiungiMentore(String username) {
        // ... (Rimane identico a come l'hai scritto tu) ...
        Optional<Utente> utente = utenteRepo.findByUsername(username);
        if(utente.isEmpty()){ throw new IllegalArgumentException("Utente inesistente"); }
        if(hackathon.utenteMembroStaff(utente.get())){ throw new IllegalArgumentException("Utente già parte dello staff");}
        if(hackathon.utentePartecipante(utente.get())){throw new IllegalArgumentException("Utente partecipante");}
        hackathon.aggiungiMentore(utente.get());
        hackathonRepo.save(hackathon);
    }
}