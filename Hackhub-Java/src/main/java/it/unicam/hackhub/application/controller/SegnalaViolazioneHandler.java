package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.*;
import it.unicam.hackhub.domain.model.eventi.ViolazioneSegnalataEvent;
import it.unicam.hackhub.domain.model.hackathon.Hackathon;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.SegnalazioneRepository;
import it.unicam.hackhub.presentation.dto.HackathonResponse;
import it.unicam.hackhub.presentation.dto.SegnalazioneRequest;
import it.unicam.hackhub.presentation.dto.TeamResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Gestore della logica di business per le segnalazioni di violazione.
 * Implementa il ruolo di "Control" nel pattern Boundary-Control-Entity.
 */
@Service
public class SegnalaViolazioneHandler {

    @Autowired
    private Sessione sessione;

    @Autowired
    private HackathonRepository hackathonRepo;

    @Autowired
    private SegnalazioneRepository segnalazioneRepo;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * Recupera la lista di hackathon a cui il mentore corrente è assegnato.
     */
    public List<HackathonResponse> getHackathonsAssegnati() {
        Utente mentore = sessione.getUtenteCorrente();
        if (mentore == null) {
            throw new IllegalStateException("Accesso negato: nessun utente in sessione.");
        }

        // Recupera solo gli hackathon attivi dove l'utente loggato è registrato come mentore
        List<Hackathon> hackathons = hackathonRepo.findAttiviByMentore(mentore.getUsername());

        return hackathons.stream()
                .map(h -> new HackathonResponse(h.getId(), h.getNome()))
                .collect(Collectors.toList());
    }

    /**
     * Recupera i team iscritti a un determinato hackathon.
     */
    public List<TeamResponse> getTeamPartecipanti(Long hackathonId) {
        Hackathon hackathon = hackathonRepo.findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato con ID: " + hackathonId));

        return hackathon.getTeamPartecipanti().stream()
                .map(t -> new TeamResponse(t.getId(), t.getNome()))
                .collect(Collectors.toList());
    }

    /**
     * Esegue l'inserimento della segnalazione e scatena l'evento di notifica.
     */
    @Transactional
    public void inserisciSegnalazione(SegnalazioneRequest request) {
        Utente mentore = sessione.getUtenteCorrente();
        if (mentore == null) {
            throw new IllegalStateException("Operazione non autorizzata.");
        }

        // 1. Validazione e recupero delle entità di dominio
        Hackathon hackathon = hackathonRepo.findById(request.hackathonId())
                .orElseThrow(() -> new IllegalArgumentException("Hackathon di riferimento non trovato."));

        Team teamSegnalato = hackathon.getTeamPartecipanti().stream()
                .filter(t -> t.getId().equals(request.teamId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Il team selezionato non partecipa a questo hackathon."));

        // 2. Creazione e persistenza della Segnalazione
        SegnalazioneViolazione segnalazione = new SegnalazioneViolazione(
                mentore,
                teamSegnalato,
                hackathon,
                request.descrizione() // Accesso al campo del record senza "get"
        );
        segnalazioneRepo.save(segnalazione);

        // 3. Notifica agli osservatori (Pattern Observer - Approccio Polimorfico)
        // Pubblichiamo l'evento specifico; il Listener lo tratterà come 'EventoNotificabile'
        ViolazioneSegnalataEvent evento = new ViolazioneSegnalataEvent(
                this,
                hackathon.getNome(),
                hackathon.getOrganizzatore()
        );

        eventPublisher.publishEvent(evento);
    }
}