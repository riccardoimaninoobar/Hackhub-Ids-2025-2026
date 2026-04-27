package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.TeamRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import it.unicam.hackhub.presentation.CliRunner;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CreazioneTeamHandlerTest {

    @MockBean
    private CliRunner cliRunner;

    @Autowired
    private CreazioneTeamHandler handler;

    @Autowired
    private Sessione sessione;
    @Autowired
    private UtenteRepository utenteRepository;
    @Autowired
    private TeamRepository teamRepo;

    private Utente utenteTest;

    @BeforeEach
    void setUp() {
        utenteTest = new Utente("testuser","test@user.it", "123456");
        utenteRepository.save(utenteTest);
    }

    @Test
    void creaTeam_Successo() {
        sessione.setUtenteCorrente(utenteTest);

        Team creato = handler.creaTeam("Team Innovazione", "IT12345678");

        assertNotNull(creato);
        assertEquals("Team Innovazione", creato.getName());
        // Verifica che l'utente sia stato aggiunto al team e viceversa
        assertTrue(creato.isMembro(utenteTest));
        assertEquals(creato, utenteTest.getTeam());
        assertTrue(teamRepo.existsByNome("Team Innovazione"));
    }



    @Test
    void creaTeam_FallisceSeUtenteNonLoggato() {
        sessione.setUtenteCorrente(null);

        assertThrows(IllegalStateException.class, () ->
                handler.creaTeam("SoloTeam", "IT12345678")
        );
    }

    @Test
    void creaTeam_FallisceSeUtenteGiaInUnTeam() {
        sessione.setUtenteCorrente(utenteTest);
        Team teamEsistente = new Team("GiaEsistente");
        teamEsistente.aggiungiMembro(utenteTest); // L'utente ha già un team

        assertThrows(IllegalStateException.class, () ->
                handler.creaTeam("NuovoTeam", "IT12345678")
        );
    }

    @Test
    void creaTeam_FallisceSeNomeTeamDuplicato() {
        sessione.setUtenteCorrente(utenteTest);
        teamRepo.save(new Team("TeamDuplicato"));

        assertThrows(IllegalArgumentException.class, () ->
                handler.creaTeam("TeamDuplicato", "IT12345678")
        );
    }
}