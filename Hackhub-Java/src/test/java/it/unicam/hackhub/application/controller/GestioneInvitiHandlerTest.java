package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.invito.state.StatoPendente;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.model.eventi.InvitoInviatoEvent; // <-- IMPORT AGGIORNATO
import it.unicam.hackhub.domain.model.invito.Invito;
import it.unicam.hackhub.domain.repository.InvitoRepository;
import it.unicam.hackhub.domain.repository.TeamRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RecordApplicationEvents
class GestioneInvitiHandlerTest {



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

    @Autowired
    private ApplicationEvents applicationEvents;

    private Utente utenteMittente;
    private Utente utenteDaInvitare;
    private Team teamTest;

    @BeforeEach
    void setUp() {
        sessione.setUtenteCorrente(null); // Assicura la pulizia dello stato tra un test e l'altro

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

        // Verifica pubblicazione evento con la nuova classe di dominio
        long countEventi = applicationEvents.stream(InvitoInviatoEvent.class).count(); // <-- ASSERZIONE AGGIORNATA
        assertEquals(1, countEventi, "Dovrebbe essere stato pubblicato esattamente un InvitoInviatoEvent");
    }

    @Test
    void elaboraInvito_FallisceSeUtenteInesistente() {
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
        // NOTA: Assicurati che questo messaggio corrisponda ESATTAMENTE a quello lanciato dal tuo Handler
        assertEquals("L'utente è già in un team", ex.getMessage());
    }

    @Test
    void elaboraInvito_FallisceSeInvitoGiaInAttesa() {
        sessione.setUtenteCorrente(utenteMittente);

        // Salviamo preventivamente un invito in stato pendente per simulare l'attesa
        Invito invitoPendente = new Invito(utenteDaInvitare, teamTest);
        invitoRepo.save(invitoPendente);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            handler.elaboraInvito("targetUser");
        });
        // NOTA: Assicurati che questo messaggio corrisponda ESATTAMENTE a quello lanciato dal tuo Handler
        assertEquals("Un invito per questo utente è già in attesa di risposta", ex.getMessage());
    }
}