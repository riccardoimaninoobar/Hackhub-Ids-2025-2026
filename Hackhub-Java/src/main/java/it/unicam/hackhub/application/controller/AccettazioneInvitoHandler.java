package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Invito;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.InvitoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccettazioneInvitoHandler {
    private final InvitoRepository invitoRepo;
    private final Sessione sessione;

    public AccettazioneInvitoHandler(InvitoRepository invitoRepo, Sessione sessione) {
        this.invitoRepo = invitoRepo;
        this.sessione = sessione;
    }

    // Corrisponde a getInvitiPendenti()[cite: 1]
    public List<Invito> getInvitiPendenti() {
        // Chiama getUtenteCorrente() sulla Sessione[cite: 1]
        Utente utente = sessione.getUtenteCorrente();
        // Chiama findPending(utente) sul Repository[cite: 1]
        return invitoRepo.findPending(utente);
    }

    // Corrisponde a accettaInvito(invito)[cite: 1]
    public void accettaInvito(Invito invito) {
        // Chiama accetta() sull'oggetto Invito[cite: 1]
        invito.accetta();
        // Chiama save(invito) sul Repository[cite: 1]
        invitoRepo.save(invito);
    }
}