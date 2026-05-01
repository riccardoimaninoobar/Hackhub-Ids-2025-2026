package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.hackathon.Hackathon;
import it.unicam.hackhub.domain.model.hackathon.HackathonBuilder;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import it.unicam.hackhub.domain.repository.TeamRepository;
import it.unicam.hackhub.presentation.CliRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // Garantisce che ogni test parta con un DB pulito effettuando il rollback alla fine
class AggiungiMentoreHandlerTest {

    @MockitoBean
    private CliRunner cliRunner;

    // Chiediamo a Spring di iniettare le vere repository collegate ad H2
    @Autowired
    private HackathonRepository hackathonRepo;

    @Autowired
    private UtenteRepository utenteRepo;

    @Autowired
    private TeamRepository teamRepo;

    // Gestiamo manualmente Sessione e Handler per assicurarci di avere istanze pulite
    private Sessione sessione;
    private AggiungiMentoreHandler handler;

    private Utente organizzatore;
    private Utente utenteGiudice;
    private Utente candidatoMentore;
    private Utente partecipante;
    private Hackathon hackathonTest;

    @BeforeEach
    void setUp() {
        // Nessun mock, usiamo le implementazioni reali
        sessione = new Sessione();
        handler = new AggiungiMentoreHandler(hackathonRepo, utenteRepo, sessione);

        // Prepariamo gli utenti
        organizzatore = new Utente("org", "org@mail.it", "pass");
        utenteGiudice = new Utente("giudice", "g@mail.it", "pass");
        candidatoMentore = new Utente("esperto", "esp@mail.it", "pass");
        partecipante = new Utente("player1", "p1@mail.it", "pass");

        // Salviamo gli utenti nel VERO database H2
        utenteRepo.save(organizzatore);
        utenteRepo.save(utenteGiudice);
        utenteRepo.save(candidatoMentore);
        utenteRepo.save(partecipante);

        // 1. CREIAMO E SALVIAMO SUBITO IL TEAM
        Team team = new Team("Team Beta");
        teamRepo.save(team); // <--- SALVIAMO PER EVITARE L'ECCEZIONE "TRANSIENT"

        // 2. AGGIUNGIAMO L'UTENTE E SINCRONIZZIAMO
        team.aggiungiMembro(partecipante);
        utenteRepo.save(partecipante); // Salviamo l'utente aggiornato col suo team

        // 3. PREPARIAMO L'HACKATHON
        hackathonTest = new HackathonBuilder()
                .assegnaNome("HackTest")
                .assegnaOrganizzatore(organizzatore)
                .assegnaGiudice(utenteGiudice)
                .build();

        hackathonTest.aggiungiTeam(team);

        hackathonRepo.save(hackathonTest);
    }

    // ==========================================================
    // TEST 1: Metodo checkOrg
    // ==========================================================

    @Test
    void checkOrg_SuccessoSeUtenteLoggatoEOrganizzatore() {
        sessione.setUtenteCorrente(organizzatore);
        assertDoesNotThrow(() -> handler.checkOrg("HackTest"));
    }

    @Test
    void checkOrg_FallisceSeUtenteNonLoggato() {
        sessione.setUtenteCorrente(null);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> handler.checkOrg("HackTest"));
        assertEquals("Devi effettuare il login per eseguire questa azione.", ex.getMessage());
    }

    @Test
    void checkOrg_FallisceSeNonOrganizzatore() {
        sessione.setUtenteCorrente(candidatoMentore);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> handler.checkOrg("HackTest"));
        assertEquals("L'utente loggato non è Organizzatore dell'Hackathon", ex.getMessage());
    }

    // ==========================================================
    // TEST 2: Metodo aggiungiMentore
    // ==========================================================

    @Test
    void aggiungiMentore_Successo() {
        sessione.setUtenteCorrente(organizzatore);
        handler.checkOrg("HackTest");

        handler.aggiungiMentore("esperto");

        // Assicuriamoci che i dati vengano sincronizzati col DB
        hackathonRepo.flush();

        assertTrue(hackathonTest.isMentore(candidatoMentore));
    }

    @Test
    void aggiungiMentore_FallisceSeGiaNelloStaff() {
        sessione.setUtenteCorrente(organizzatore);
        handler.checkOrg("HackTest");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> handler.aggiungiMentore("giudice"));
        assertEquals("Utente già parte dello staff", ex.getMessage());
    }

    @Test
    void aggiungiMentore_FallisceSeUtentePartecipante() {
        sessione.setUtenteCorrente(organizzatore);
        handler.checkOrg("HackTest");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> handler.aggiungiMentore("player1"));
        assertEquals("Utente partecipante", ex.getMessage());
    }
}