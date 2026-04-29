package it.unicam.hackhub.application.controller;
import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Hackathon;
import it.unicam.hackhub.domain.model.Sottomissione;
import it.unicam.hackhub.domain.model.StatoInValutazione;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
@Service
@Transactional
public class ValutareSottomissioneHandler {
    private final HackathonRepository hackathonRepo;
    private final Sessione sessione;
    public ValutareSottomissioneHandler(HackathonRepository hackathonRepo, Sessione sessione) {
        this.hackathonRepo = hackathonRepo;
        this.sessione = sessione;
    }
    public Set<Hackathon> getHackathonsGiudice() {
        Utente corrente = sessione.getUtenteCorrente();
        if (corrente == null) {
            throw new IllegalStateException("Devi effettuare il login.");
        }
        Set<Hackathon> hackathons = hackathonRepo.findAll().stream()
                .peek(Hackathon::aggiornaStato)
                .filter(h -> h.isGiudice(corrente))
                .filter(h -> h.getStato() instanceof StatoInValutazione)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (hackathons.isEmpty()) {
            throw new IllegalStateException("Al momento non hai sottomissioni da valutare.");
        }
        return hackathons;
    }
    public Set<Sottomissione> getSottomissioni(String nomeHackathon) {
        Utente corrente = sessione.getUtenteCorrente();
        if (corrente == null) {
            throw new IllegalStateException("Devi effettuare il login.");
        }
        Hackathon hackathon = hackathonRepo.findByNome(nomeHackathon)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon inesistente."));
        hackathon.aggiornaStato();
        if (!hackathon.isGiudice(corrente)) {
            throw new IllegalArgumentException("Non sei autorizzato a valutare le sottomissioni di questo Hackathon.");
        }
        if (!(hackathon.getStato() instanceof StatoInValutazione)) {
            throw new IllegalStateException("L'Hackathon non è in stato di valutazione.");
        }
        Set<Sottomissione> sottomissioni = hackathon.getSottomissioni();
        if (sottomissioni.isEmpty()) {
            throw new IllegalStateException("Al momento non ci sono sottomissioni da valutare.");
        }
        return sottomissioni;
    }
    public void assegnaPunteggio(String nomeHackathon, Long idSottomissione, int punteggio) {
        Utente corrente = sessione.getUtenteCorrente();
        if (corrente == null) {
            throw new IllegalStateException("Devi effettuare il login.");
        }
        if (punteggio < 1 || punteggio > 10) {
            throw new IllegalArgumentException("Votazione non valida, deve essere un numero compreso tra 1 e 10.");
        }
        Hackathon hackathon = hackathonRepo.findByNome(nomeHackathon)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon inesistente."));
        hackathon.aggiornaStato();
        if (!hackathon.isGiudice(corrente)) {
            throw new IllegalArgumentException("Non sei autorizzato a valutare questa sottomissione.");
        }
        if (!(hackathon.getStato() instanceof StatoInValutazione)) {
            throw new IllegalStateException("L'Hackathon non è in stato di valutazione.");
        }
        Sottomissione sottomissione = hackathon.getSottomissioni().stream()
                .filter(s -> idSottomissione.equals(s.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Sottomissione non trovata per questo Hackathon."));
        sottomissione.setPunteggio(punteggio);
        hackathonRepo.save(hackathon);
    }
}
