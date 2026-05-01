package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.SegnalazioneViolazione;
import it.unicam.hackhub.domain.model.StatoSegnalazione;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.SegnalazioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GestisciViolazioneHandler {

    @Autowired
    private Sessione sessione;

    @Autowired
    private SegnalazioneRepository segnalazioneRepo;

    @Transactional
    public void gestisciViolazione(Long segnalazioneId, StatoSegnalazione esito, String motivazione) {
        Utente organizzatore = sessione.getUtenteCorrente();
        if (organizzatore == null) {
            throw new IllegalStateException("Nessun utente autenticato in sessione.");
        }

        // Recupera la segnalazione dal DB
        SegnalazioneViolazione segnalazione = segnalazioneRepo.findById(segnalazioneId)
                .orElseThrow(() -> new IllegalArgumentException("Segnalazione non trovata."));

        // Verifica (opzionale ma consigliata) che l'utente loggato sia l'organizzatore dell'Hackathon
        if (!segnalazione.getHackathon().isOrganizzatore(organizzatore)) {
            throw new IllegalStateException("Non sei autorizzato a gestire questa segnalazione.");
        }

        // Applica i cambiamenti di dominio
        segnalazione.setProvvedimento(esito, motivazione);

        // Se l'esito è una squalifica (es. ACCOLTA), deleghiamo all'entità il compito di squalificare il team
        if (esito == StatoSegnalazione.ACCOLTA) {
            segnalazione.squalificaTeam();
        }

        // Salva le modifiche
        segnalazioneRepo.save(segnalazione);
    }
}