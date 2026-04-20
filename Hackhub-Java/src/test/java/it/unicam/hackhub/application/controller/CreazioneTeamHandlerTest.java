package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.TeamRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryTeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreazioneTeamHandlerTest {

    private CreazioneTeamHandler handler;
    private Sessione sessione;
    private TeamRepository teamRepo;
    private Utente utenteTest;

    @BeforeEach
    void setUp() {
        sessione = new Sessione(null);
        teamRepo = new InMemoryTeamRepository();
        handler = new CreazioneTeamHandler(teamRepo, sessione);

        utenteTest = new Utente("leader", "leader@mail.com", "pass");
    }

    @Test
    void creaTeam_Successo() {
        sessione.setUtenteCorrente(utenteTest);

        Team creato = handler.creaTeam("Team Innovazione");

        assertNotNull(creato);
        assertEquals("Team Innovazione", creato.getName());
        // Verifica che l'utente sia stato aggiunto al team e viceversa
        assertTrue(creato.isMembro(utenteTest));
        assertEquals(creato, utenteTest.getTeam());
        assertTrue(teamRepo.existsById("Team Innovazione"));
    }

    @Test
    void creaTeam_FallisceSeUtenteNonLoggato() {
        sessione.setUtenteCorrente(null);

        assertThrows(IllegalStateException.class, () ->
                handler.creaTeam("SoloTeam")
        );
    }

    @Test
    void creaTeam_FallisceSeUtenteGiaInUnTeam() {
        sessione.setUtenteCorrente(utenteTest);
        Team teamEsistente = new Team("GiaEsistente");
        teamEsistente.aggiungiMembro(utenteTest); // L'utente ha già un team

        assertThrows(IllegalStateException.class, () ->
                handler.creaTeam("NuovoTeam")
        );
    }

    @Test
    void creaTeam_FallisceSeNomeTeamDuplicato() {
        sessione.setUtenteCorrente(utenteTest);
        teamRepo.save(new Team("TeamDuplicato"));

        assertThrows(IllegalArgumentException.class, () ->
                handler.creaTeam("TeamDuplicato")
        );
    }
}