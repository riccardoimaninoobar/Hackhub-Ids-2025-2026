package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.SegnalazioneViolazione;
import it.unicam.hackhub.domain.model.EsitoSegnalazione;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.model.eventi.ViolazioneGestitaEvent;
import it.unicam.hackhub.domain.repository.SegnalazioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GestisciViolazioneHandler {

    @Autowired
    private Sessione sessione;

    @Autowired
    private SegnalazioneRepository segnalazioneRepo;

    // Aggiunto il Publisher per le notifiche
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    public void gestisciViolazione(Long segnalazioneId, EsitoSegnalazione esito, String motivazione) {
        Utente organizzatore = sessione.getUtenteCorrente();
        if (organizzatore == null) {
            throw new IllegalStateException("Nessun utente autenticato in sessione.");
        }

        // Recupera la segnalazione dal DB (come da findById nel diagramma)
        SegnalazioneViolazione segnalazione = segnalazioneRepo.findById(segnalazioneId)
                .orElseThrow(() -> new IllegalArgumentException("Segnalazione non trovata."));

        // Verifica (opzionale ma consigliata) che l'utente loggato sia l'organizzatore dell'Hackathon
        if (!segnalazione.getHackathon().isOrganizzatore(organizzatore)) {
            throw new IllegalStateException("Non sei autorizzato a gestire questa segnalazione.");
        }

        // Applica i cambiamenti di dominio (la logica di squalifica è delegata internamente)
        segnalazione.setProvvedimento(esito, motivazione);

        // Salva le modifiche
        segnalazioneRepo.save(segnalazione);

        // --- Logica di Notifica ---
        // Recupero i dati per l'evento delegando i getter all'entità come da diagramma
        String nomeHackathon = segnalazione.getNomeHackathon();
        Utente mentore = segnalazione.getMentore();

        // Creazione e pubblicazione dell'evento
        ViolazioneGestitaEvent evento = new ViolazioneGestitaEvent(this, nomeHackathon, mentore);
        eventPublisher.publishEvent(evento);
    }
}