package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.InvitoRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryInvitoRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryUtenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GestioneInvitiHandlerTest {

    private GestioneInvitiHandler handler;
    private Sessione sessione;
    private UtenteRepository utenteRepo;
    private InvitoRepository invitoRepo;

    private Utente utenteMittente;
    private Utente utenteDaInvitare;
    private Team teamTest;

    @BeforeEach
    void setUp() {
        sessione = new Sessione(null);
        utenteRepo = new InMemoryUtenteRepository();
        invitoRepo = new InMemoryInvitoRepository();
        handler = new GestioneInvitiHandler(utenteRepo, invitoRepo, sessione);

        utenteMittente = new Utente("leader", "leader@mail.com", "pass");
        teamTest = new Team("Team Alpha");
        teamTest.aggiungiMembro(utenteMittente);

        utenteDaInvitare = new Utente("targetUser", "target@mail.com", "pass");

        utenteRepo.save(utenteMittente);
        utenteRepo.save(utenteDaInvitare);
    }

    @Test
    void elaboraInvito_Successo() {
        // Arrange
        sessione.setUtenteCorrente(utenteMittente);

        // Act
        assertDoesNotThrow(() -> handler.elaboraInvito("targetUser"));

        // Assert
        assertTrue(invitoRepo.existsActiveInvitation(utenteDaInvitare, teamTest, "IN_ATTESA"),
                "L'invito dovrebbe essere salvato con stato IN_ATTESA nel repository");
    }

    @Test
    void elaboraInvito_FallisceSeUtenteNonEsiste() {
        sessione.setUtenteCorrente(utenteMittente);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            handler.elaboraInvito("ghostUser");
        });
        assertEquals("Utente inesistente", ex.getMessage());
    }

    @Test
    void elaboraInvito_FallisceSeUtenteGiaInTeam() {
        sessione.setUtenteCorrente(utenteMittente);
        
        Team altroTeam = new Team("Team Beta");
        altroTeam.aggiungiMembro(utenteDaInvitare);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            handler.elaboraInvito("targetUser");
        });
        assertEquals("L'utente è già in un team", ex.getMessage());
    }
}