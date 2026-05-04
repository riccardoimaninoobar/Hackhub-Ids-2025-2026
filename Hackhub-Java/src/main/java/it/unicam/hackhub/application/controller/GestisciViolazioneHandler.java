package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.EsitoSegnalazione;
import it.unicam.hackhub.domain.model.SegnalazioneViolazione;
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
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    public void gestisciViolazione(Long segnalazioneId, EsitoSegnalazione esito, String motivazione) {
        Utente organizzatore = sessione.getUtenteCorrente();
        if (organizzatore == null) {
            throw new IllegalStateException("Nessun utente autenticato in sessione.");
        }

        SegnalazioneViolazione segnalazione = segnalazioneRepo.findById(segnalazioneId)
                .orElseThrow(() -> new IllegalArgumentException("Segnalazione non trovata."));

        if (!segnalazione.getHackathon().isOrganizzatore(organizzatore)) {
            throw new IllegalStateException("Non sei autorizzato a gestire questa segnalazione.");
        }

        if (motivazione == null || motivazione.trim().isEmpty()) {
            throw new IllegalArgumentException("La motivazione non può essere vuota.");
        }

        segnalazione.setProvvedimento(esito, motivazione.trim());
        segnalazioneRepo.save(segnalazione);

        ViolazioneGestitaEvent evento = new ViolazioneGestitaEvent(
                this,
                segnalazione.getHackathon().getNome(),
                segnalazione.getMentore()
        );

        eventPublisher.publishEvent(evento);
    }
}