package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.hackathon.Hackathon;
import it.unicam.hackhub.domain.model.Notifica;
import it.unicam.hackhub.domain.model.RichiestaSupporto;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.model.NotificaEvent;
import it.unicam.hackhub.domain.repository.RichiestaSupportoRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Transactional
public class RichiestaSupportoHandler {
    private final Sessione sessione;
    private final RichiestaSupportoRepository richiestaRepo;
    private final UtenteRepository utenteRepo;
    private final ApplicationEventPublisher eventPublisher;

    public RichiestaSupportoHandler(Sessione sessione, RichiestaSupportoRepository richiestaRepo,
                                    UtenteRepository utenteRepo, ApplicationEventPublisher eventPublisher) {
        this.sessione = sessione;
        this.richiestaRepo = richiestaRepo;
        this.utenteRepo = utenteRepo;
        this.eventPublisher = eventPublisher;
    }

    // 1. Recupera la lista degli hackathon del team
    public Set<Hackathon> getHackathons() {
        Utente corrente = sessione.getUtenteCorrente();
        if (corrente == null) throw new IllegalStateException("Devi effettuare il login.");

        Utente u = utenteRepo.findById(corrente.getId())
                .orElseThrow(() -> new IllegalStateException("Utente non trovato."));

        Team t = u.getTeam();
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
        // Validazione preventiva
        convalidaDescrizione(desc);

        Utente corrente = sessione.getUtenteCorrente();
        Utente u = utenteRepo.findById(corrente.getId()).get();
        Team t = u.getTeam();

        RichiestaSupporto richiesta = new RichiestaSupporto(t, h, desc);
        richiestaRepo.save(richiesta);

        for (Utente mentore : h.getMentori()) {
            Notifica notifica = new Notifica(mentore, "Nuova Richiesta di Supporto",
                    "Il team " + t.getNome() + " ha richiesto supporto: " + desc);
            eventPublisher.publishEvent(new NotificaEvent(notifica));
        }
    }
}