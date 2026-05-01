package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.SegnalazioneViolazione;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.SegnalazioneRepository;
import it.unicam.hackhub.presentation.dto.SegnalazioneResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VisualizzaViolazioniHandler {

    @Autowired
    private Sessione sessione;

    @Autowired
    private SegnalazioneRepository segnalazioneRepo;

    @Transactional(readOnly = true)
    public List<SegnalazioneResponse> getViolazioni() {
        Utente organizzatore = sessione.getUtenteCorrente();
        if (organizzatore == null) {
            throw new IllegalStateException("Nessun utente autenticato in sessione.");
        }

        // Il repository recupera solo le segnalazioni APERTE per gli hackathon di questo organizzatore
        List<SegnalazioneViolazione> segnalazioni = segnalazioneRepo.findAperteByOrganizzatore(organizzatore.getUsername());

        return segnalazioni.stream()
                .map(s -> new SegnalazioneResponse(
                        s.getId(),
                        s.getHackathon().getNome(),
                        s.getTeam().getNome(),
                        s.getDescrizione(),
                        s.getStato().name()
                ))
                .collect(Collectors.toList());
    }
}