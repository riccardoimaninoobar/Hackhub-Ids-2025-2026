package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.EsitoSegnalazione;
import it.unicam.hackhub.domain.model.SegnalazioneViolazione;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.model.eventi.ViolazioneGestitaEvent;
import it.unicam.hackhub.domain.model.hackathon.Hackathon;
import it.unicam.hackhub.domain.model.hackathon.HackathonBuilder;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.SegnalazioneRepository;
import it.unicam.hackhub.domain.repository.TeamRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RecordApplicationEvents
class GestisciViolazioniHandlerTest {

    @Autowired
    private ApplicationEvents applicationEvents;

    @Autowired
    private GestisciViolazioneHandler handler;

    @Autowired
    private Sessione sessione;

    @Autowired
    private UtenteRepository utenteRepo;

    @Autowired
    private TeamRepository teamRepo;

    @Autowired
    private HackathonRepository hackathonRepo;

    @Autowired
    private SegnalazioneRepository segnalazioneRepo;

    private Utente organizzatore;
    private Utente hacker;
    private Utente mentore;
    private Team teamBeta;
    private Hackathon hackathon;
    private SegnalazioneViolazione segnalazioneAperta;

    @BeforeEach
    void setUp() {
        sessione.setUtenteCorrente(null);

        organizzatore = new Utente("org1", "org1mail.com", "pass");
        hacker = new Utente("hacker", "hackermail.com", "pass");
        mentore = new Utente("mentore1", "m1mail.com", "pass");

        utenteRepo.save(organizzatore);
        utenteRepo.save(hacker);
        utenteRepo.save(mentore);

        teamBeta = new Team("Team Beta");
        teamRepo.save(teamBeta);

        hackathon = new HackathonBuilder()
                .assegnaNome("Hack Finale")
                .assegnaOrganizzatore(organizzatore)
                .build();
        hackathon.aggiungiTeam(teamBeta);
        hackathonRepo.save(hackathon);

        segnalazioneAperta = new SegnalazioneViolazione(
                mentore,
                teamBeta,
                hackathon,
                "Violazione rilevata"
        );
        segnalazioneRepo.save(segnalazioneAperta);
    }

    @Test
    void gestisciViolazioneSuccessoAggiornaStatoEPubblicaEvento() {
        sessione.setUtenteCorrente(organizzatore);

        assertDoesNotThrow(() ->
                handler.gestisciViolazione(
                        segnalazioneAperta.getId(),
                        EsitoSegnalazione.ACCOLTA,
                        "Regolamento violato"
                )
        );

        SegnalazioneViolazione salvata = segnalazioneRepo.findById(segnalazioneAperta.getId())
                .orElseThrow();

        assertEquals(EsitoSegnalazione.ACCOLTA, salvata.getStato());

        long conteggioEventi = applicationEvents.stream(ViolazioneGestitaEvent.class).count();
        assertEquals(1, conteggioEventi,
                "Dovrebbe essere stato pubblicato esattamente un evento");
    }

    @Test
    void gestisciViolazioneFallisceSeNonLoggato() {
        sessione.setUtenteCorrente(null);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                handler.gestisciViolazione(
                        segnalazioneAperta.getId(),
                        EsitoSegnalazione.RESPINTA,
                        "Non rilevante"
                )
        );
        assertEquals("Nessun utente autenticato in sessione.", ex.getMessage());

        long conteggioEventi = applicationEvents.stream(ViolazioneGestitaEvent.class).count();
        assertEquals(0, conteggioEventi,
                "Nessun evento deve essere pubblicato in caso di errore");
    }

    @Test
    void gestisciViolazioneFallisceSeUtenteNonEOrganizzatoreDelHackathon() {
        sessione.setUtenteCorrente(hacker);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                handler.gestisciViolazione(
                        segnalazioneAperta.getId(),
                        EsitoSegnalazione.RESPINTA,
                        "Non rilevante"
                )
        );
        assertEquals("Non sei autorizzato a gestire questa segnalazione.", ex.getMessage());
    }

    @Test
    void gestisciViolazioneFallisceSeSegnalazioneNonEsiste() {
        sessione.setUtenteCorrente(organizzatore);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                handler.gestisciViolazione(
                        9999L,
                        EsitoSegnalazione.ACCOLTA,
                        "Test"
                )
        );
        assertEquals("Segnalazione non trovata.", ex.getMessage());
    }
}