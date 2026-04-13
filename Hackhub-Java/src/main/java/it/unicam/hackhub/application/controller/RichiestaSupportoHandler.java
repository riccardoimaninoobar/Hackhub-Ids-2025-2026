package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Hackathon;
import it.unicam.hackhub.domain.model.RichiestaSupporto;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.RichiestaSupportoRepository;

import java.util.Set;

public class RichiestaSupportoHandler {
    private final Sessione sessione;
    private final RichiestaSupportoRepository richiestaRepo;

    public RichiestaSupportoHandler(Sessione sessione, RichiestaSupportoRepository richiestaRepo) {
        this.sessione = sessione;
        this.richiestaRepo = richiestaRepo;
    }

    // 1. Recupera la lista degli hackathon del team
    public Set<Hackathon> getHackathons() {
        Utente corrente = sessione.getUtenteCorrente();
        if (corrente == null) throw new IllegalStateException("Devi effettuare il login.");

        Team t = corrente.getTeam();
        if (t == null) throw new IllegalStateException("Devi far parte di un team per richiedere supporto.");

        Set<Hackathon> hs = t.getHackathonInCorso();
        if (hs.isEmpty()) {
            throw new IllegalStateException("Il tuo team non è iscritto ad alcun Hackathon attualmente in corso.");
        }

        return hs;
    }

    // 2. Valida la descrizione (gestione del blocco LOOP / ALT)
    public void convalidaDescrizione(String desc) {
        if (desc == null || desc.trim().length() < 20) {
            throw new IllegalArgumentException("La descrizione deve contenere almeno 20 caratteri.");
        }
    }

    // 3. Crea e salva la richiesta
    public void registraRichiestaSupporto(Hackathon h, String desc) {
        Team t = sessione.getUtenteCorrente().getTeam(); // Recuperiamo il team in modo sicuro
        RichiestaSupporto richiesta = new RichiestaSupporto(t, h, desc);
        richiestaRepo.save(richiesta);
    }
}