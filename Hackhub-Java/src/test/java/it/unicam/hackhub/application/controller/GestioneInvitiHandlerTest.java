package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.invito.state.StatoPendente;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.InvitoRepository;
import it.unicam.hackhub.domain.repository.TeamRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import it.unicam.hackhub.presentation.CliRunner;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class GestioneInvitiHandlerTest {
    @MockitoBean
    private CliRunner cliRunner;
    @Autowired
    private GestioneInvitiHandler handler;
    @Autowired
    private Sessione sessione;
    @Autowired
    private UtenteRepository utenteRepo;
    @Autowired
    private InvitoRepository invitoRepo;

    @Autowired
    private TeamRepository teamRepo;

    private Utente utenteMittente;
    private Utente utenteDaInvitare;
    private Team teamTest;


    @BeforeEach
    void setUp() {
        utenteMittente = new Utente("leader", "leader@mail.com", "pass");
        teamTest = new Team("Team Alpha");
        teamTest.aggiungiMembro(utenteMittente);
        teamRepo.save(teamTest);

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
        assertTrue(invitoRepo.existsByInvitatoAndTeamMittenteAndStato(utenteDaInvitare, teamTest, new StatoPendente()),
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

        // 1. Creiamo e SALVIAMO il nuovo team nel database
        Team altroTeam = new Team("Team Beta");
        teamRepo.save(altroTeam);

        // 2. Aggiungiamo l'utente e AGGIORNIAMO il suo record nel database
        altroTeam.aggiungiMembro(utenteDaInvitare);
        utenteRepo.save(utenteDaInvitare);

        // 3. Ora l'handler troverà i dati corretti e coerenti nel DB
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            handler.elaboraInvito("targetUser");
        });
        assertEquals("L'utente è già in un team", ex.getMessage());
    }
}