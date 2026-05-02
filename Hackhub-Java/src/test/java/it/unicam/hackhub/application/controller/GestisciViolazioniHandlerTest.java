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
import it.unicam.hackhub.presentation.CliRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RecordApplicationEvents
class GestisciViolazioneHandlerTest {

    @MockitoBean
    private CliRunner cliRunner;

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
    private Utente hacker; // un utente che non c'entra niente
    private SegnalazioneViolazione segnalazioneAperta;

    @BeforeEach
    void setUp() {
        sessione.setUtenteCorrente(null);

        organizzatore = new Utente("org1", "org1@mail.com", "pass");
        hacker = new Utente("hacker", "hacker@mail.com", "pass");
        Utente mentore = new Utente("mentore1", "m1@mail.com", "pass");

        utenteRepo.save(organizzatore);
        utenteRepo.save(hacker);
        utenteRepo.save(mentore);

        Team teamBeta = new Team("Team Beta");
        teamRepo.save(teamBeta);

        Hackathon hackathon = new HackathonBuilder()
                .assegnaNome("Hack Finale")
                .assegnaOrganizzatore(organizzatore)
                .build();
        hackathon.aggiungiTeam(teamBeta);
        hackathonRepo.save(hackathon);

        segnalazioneAperta = new SegnalazioneViolazione(mentore, teamBeta, hackathon, "Violazione rilevata");
        segnalazioneRepo.save(segnalazioneAperta);
    }

    @Test
    void gestisciViolazione_SuccessoAggiornaStatoEPubblicaEvento() {
        sessione.setUtenteCorrente(organizzatore);

        assertDoesNotThrow(() ->
                handler.gestisciViolazione(segnalazioneAperta.getId(), EsitoSegnalazione.ACCOLTA, "Regolamento violato")
        );

        // 1. Verifichiamo il database
        SegnalazioneViolazione salvata = segnalazioneRepo.findById(segnalazioneAperta.getId()).orElseThrow();
        assertEquals(EsitoSegnalazione.ACCOLTA, salvata.getStato());

        // 2. LA NUOVA VERIFICA DELL'EVENTO
        long conteggioEventi = applicationEvents.stream(ViolazioneGestitaEvent.class).count();
        assertEquals(1, conteggioEventi, "Dovrebbe essere stato pubblicato esattamente un evento");
    }

    @Test
    void gestisciViolazione_FallisceSeNonLoggato() {
        sessione.setUtenteCorrente(null);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                handler.gestisciViolazione(segnalazioneAperta.getId(), EsitoSegnalazione.RESPINTA, "Non rilevante")
        );
        assertEquals("Nessun utente autenticato in sessione.", ex.getMessage());

        // Verifica che in caso di errore, l'evento NON parta (il conteggio deve essere 0)
        long conteggioEventi = applicationEvents.stream(ViolazioneGestitaEvent.class).count();
        assertEquals(0, conteggioEventi, "Nessun evento deve essere pubblicato in caso di errore");
    }

    @Test
    void gestisciViolazione_FallisceSeUtenteNonEOrganizzatoreDelHackathon() {
        sessione.setUtenteCorrente(hacker);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                handler.gestisciViolazione(segnalazioneAperta.getId(), EsitoSegnalazione.RESPINTA, "Non rilevante")
        );
        assertEquals("Non sei autorizzato a gestire questa segnalazione.", ex.getMessage());
    }

    @Test
    void gestisciViolazione_FallisceSeSegnalazioneNonEsiste() {
        sessione.setUtenteCorrente(organizzatore);

        assertThrows(IllegalArgumentException.class, () ->
                handler.gestisciViolazione(9999L, EsitoSegnalazione.ACCOLTA, "Test")
        );
    }
}