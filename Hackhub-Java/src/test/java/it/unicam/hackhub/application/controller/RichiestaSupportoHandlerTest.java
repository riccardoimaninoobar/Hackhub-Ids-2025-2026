package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.*;
import it.unicam.hackhub.domain.repository.RichiestaSupportoRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryRichiestaSupportoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RichiestaSupportoHandlerTest {

    private RichiestaSupportoHandler handler;
    private Sessione sessione;
    private RichiestaSupportoRepository richiestaRepo;

    // Oggetti di dominio condivisi per i test
    private Utente utenteTest;
    private Team teamTest;
    private Hackathon hackathonInCorso;

    @BeforeEach
    void setUp() {
        // Inizializziamo le dipendenze
        sessione = new Sessione(null);
        richiestaRepo = new InMemoryRichiestaSupportoRepository();
        handler = new RichiestaSupportoHandler(sessione, richiestaRepo);

        // Prepariamo i dati fittizi
        utenteTest = new Utente("testUser", "test@mail.com", "password123");
        teamTest = new Team("Team Alpha");
        teamTest.aggiungiMembro(utenteTest);

        // Creiamo un Hackathon usando il tuo Builder
        Utente org = new Utente("org", "org@mail.com", "pass");
        HackathonBuilder builder = new HackathonBuilder()
                .assegnaNome("Hackathon Nazionale")
                .assegnaRegolamento("Regole standard")
                .assegnaOrganizzatore(org);

        hackathonInCorso = builder.build();

        // Forziamo lo stato "In corso" (tramite il tuo Pattern State!)
        hackathonInCorso.setStato(new StatoInCorso());
    }

    // ==========================================================
    // TEST 1: Metodo getHackathons()
    // ==========================================================

    @Test
    void getHackathons_Successo() {
        // Arrange
        sessione.setUtenteCorrente(utenteTest);
        hackathonInCorso.aggiungiTeam(teamTest); // Iscrive il team all'evento

        // Act
        Set<Hackathon> result = handler.getHackathons();

        // Assert
        assertFalse(result.isEmpty(), "Il set non dovrebbe essere vuoto");
        assertTrue(result.contains(hackathonInCorso), "Il set dovrebbe contenere l'hackathon in corso");
    }

    @Test
    void getHackathons_LanciaEccezioneSeNessunUtenteLoggato() {
        // Arrange
        sessione.setUtenteCorrente(null);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            handler.getHackathons();
        });
        assertEquals("Devi effettuare il login.", exception.getMessage());
    }

    @Test
    void getHackathons_LanciaEccezioneSeUtenteSenzaTeam() {
        // Arrange
        Utente utenteSenzaTeam = new Utente("solo", "solo@mail.com", "pass");
        sessione.setUtenteCorrente(utenteSenzaTeam);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            handler.getHackathons();
        }, "Dovrebbe lanciare eccezione se l'utente non fa parte di alcun team.");
    }

    @Test
    void getHackathons_LanciaEccezioneSeNessunHackathonInCorso() {
        // Arrange
        sessione.setUtenteCorrente(utenteTest);

        // Creiamo un hackathon che è rimasto "In Iscrizione" e ci iscriviamo il team
        Hackathon hackInIscrizione = new HackathonBuilder()
                .assegnaNome("Hack Futuro")
                .assegnaOrganizzatore(new Utente("org2", "o@mail.com", "pass"))
                .build();
        hackInIscrizione.aggiungiTeam(teamTest);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            handler.getHackathons();
        });
        assertEquals("Il tuo team non è iscritto ad alcun Hackathon attualmente in corso.", exception.getMessage());
    }

    // ==========================================================
    // TEST 2: Metodo convalidaDescrizione(desc)
    // ==========================================================

    @Test
    void convalidaDescrizione_NonLanciaEccezioneSeValida() {
        String descrizioneLunga = "Questa è una descrizione molto dettagliata del problema tecnico, sicuramente maggiore di venti caratteri.";
        assertDoesNotThrow(() -> handler.convalidaDescrizione(descrizioneLunga));
    }

    @Test
    void convalidaDescrizione_LanciaEccezioneSeTroppoCorta() {
        String descrizioneCorta = "Aiuto non va nulla"; // 18 caratteri
        assertThrows(IllegalArgumentException.class, () -> {
            handler.convalidaDescrizione(descrizioneCorta);
        });
    }

    @Test
    void convalidaDescrizione_LanciaEccezioneSeNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            handler.convalidaDescrizione(null);
        });
    }

    // ==========================================================
    // TEST 3: Metodo registraRichiestaSupporto(h, desc)
    // ==========================================================

    @Test
    void registraRichiestaSupporto_SalvaCorrettamenteNelRepository() {
        // Arrange
        sessione.setUtenteCorrente(utenteTest);
        String descrizioneValida = "Ho un problema con il database che non si connette alla porta 8080.";

        // Act
        handler.registraRichiestaSupporto(hackathonInCorso, descrizioneValida);

        // Assert
        assertEquals(1, richiestaRepo.findAll().size(), "Ci dovrebbe essere esattamente una richiesta salvata");

        RichiestaSupporto salvata = richiestaRepo.findAll().get(0);
        assertNotNull(salvata.getId());
        assertEquals(teamTest, salvata.getTeam());
        assertEquals(hackathonInCorso, salvata.getHackathon());
        assertEquals(descrizioneValida, salvata.getDescrizione());
    }
}