package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Hackathon;
import it.unicam.hackhub.domain.model.Sottomissione;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Transactional
public class CaricaSottomissioneHandler {

    private final HackathonRepository hackathonRepo;
    private final Sessione sessione;
    private final UtenteRepository utenteRepository;

    public CaricaSottomissioneHandler(HackathonRepository hackathonRepo, Sessione sessione, UtenteRepository utenteRepository) {
        this.hackathonRepo = hackathonRepo;
        this.sessione = sessione;
        this.utenteRepository = utenteRepository;
    }

    public Set<Hackathon> getHackathonInCorso() {
        Utente corrente = sessione.getUtenteCorrente();
        if (corrente == null) throw new IllegalStateException("Devi effettuare il login.");

        // 1. Ricarico l'utente fresco
        Utente u = utenteRepository.findById(corrente.getId())
                .orElseThrow(() -> new IllegalStateException("Utente non più valido nel DB."));

        // 2. Uso l'utente FRESCO ('u') per prendere il team
        Team t = u.getTeam();
        if (t == null) throw new IllegalStateException("Devi far parte di un team.");

        // Ora la lista lazy funzionerà!
        return t.getHackathonInCorso();
    }

    public void caricaSottomissione(Hackathon h, String link) {
        Utente corrente = sessione.getUtenteCorrente();

        // Ricarico l'utente fresco anche qui per evitare di salvare entità detached
        Utente u = utenteRepository.findById(corrente.getId())
                .orElseThrow(() -> new IllegalStateException("Utente non più valido nel DB."));

        Team teamFresco = u.getTeam();

        Sottomissione s = new Sottomissione("Sottomissione_" + h.getNome(), link, teamFresco);

        h.caricaSottomissione(s);

        hackathonRepo.save(h);
    }
}