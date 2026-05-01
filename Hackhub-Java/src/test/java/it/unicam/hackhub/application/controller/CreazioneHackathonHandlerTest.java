package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.hackathon.Hackathon;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import it.unicam.hackhub.presentation.CliRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // Accende Spring e il Database!
@Transactional  // Svuota il DB alla fine di ogni singolo test
class CreazioneHackathonHandlerTest {

    @MockitoBean
    private CliRunner cliRunner;
    // Spring "inietta" automaticamente tutto. Niente più 'new' o 'mock'!
    @Autowired private CreazioneHackathonHandler handler;
    @Autowired private Sessione sessione;
    @Autowired private HackathonRepository hackathonRepo;
    @Autowired private UtenteRepository utenteRepo;

    private Utente organizzatore;
    private Utente utenteGiudice;
    private Utente mentore;

    @BeforeEach
    void setUp() {
        // Creiamo gli utenti e li salviamo nel VERO database H2
        organizzatore = new Utente("org", "org@mail.it", "pass");
        utenteGiudice = new Utente("giudice", "g@mail.it", "pass");
        mentore = new Utente("esperto", "esp@mail.it", "pass");

        utenteRepo.save(organizzatore);
        utenteRepo.save(utenteGiudice);
        utenteRepo.save(mentore);
    }

    // ==========================================================
    // TEST 1: Creazione Base
    // ==========================================================

    @Test
    void creaHackathonBase_Successo() {
        sessione.setUtenteCorrente(organizzatore);

        assertDoesNotThrow(() -> {
            handler.creaHackathonBase("GlobalHack", "Regole", LocalDate.now(), LocalDate.now(), LocalDate.now().plusDays(2), "Roma", 5, new BigDecimal("1000"));
        });

        // Verifichiamo l'esistenza usando il NOME (Business Key)
        assertFalse(hackathonRepo.existsByNome("GlobalHack"));
    }

    @Test
    void creaHackathonBase_FallisceSeGiaEsiste() {
        sessione.setUtenteCorrente(organizzatore);

        handler.creaHackathonBase("HackEsistente", "R", LocalDate.now(), LocalDate.now(), LocalDate.now().plusDays(1), "L", 2, new BigDecimal("10"));
        handler.assegnaGiudice("giudice");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            handler.creaHackathonBase("HackEsistente", "Altre", LocalDate.now(), LocalDate.now(), LocalDate.now().plusDays(1), "Altro", 2, new BigDecimal("20"));
        });
        assertEquals("Hackathon con questo nome già esistente.", ex.getMessage());
    }

    // ==========================================================
    // TEST 2: Assegna Giudice e Completamento Creazione
    // ==========================================================

    @Test
    void assegnaGiudice_CompletaECreaHackathon() {
        sessione.setUtenteCorrente(organizzatore);
        handler.creaHackathonBase("Hack1", "R", LocalDate.now(), LocalDate.now(), LocalDate.now().plusDays(1), "Roma", 5, new BigDecimal("1000"));

        boolean result = handler.assegnaGiudice("giudice");

        assertTrue(result, "L'assegnazione del giudice dovrebbe restituire true se l'utente esiste.");

        // Ripeschiamo l'Hackathon dal DB usando il NOME
        Optional<Hackathon> salvato = hackathonRepo.findByNome("Hack1");

        assertTrue(salvato.isPresent());
        assertEquals("Hack1", salvato.get().getNome());
        assertTrue(salvato.get().isOrganizzatore(organizzatore));
        assertTrue(salvato.get().isGiudice(utenteGiudice));
    }

    // ==========================================================
    // TEST 3: Assegna Mentore (Delega a AggiungiMentoreHandler)
    // ==========================================================

    @Test
    void assegnaMentore_AggiungeCorrettamenteTramiteDelega() {
        sessione.setUtenteCorrente(organizzatore);
        handler.creaHackathonBase("HackFinale", "R", LocalDate.now(), LocalDate.now(), LocalDate.now().plusDays(1), "Roma", 5, new BigDecimal("1000"));
        handler.assegnaGiudice("giudice");

        assertDoesNotThrow(() -> {
            handler.assegnaMentore("esperto");
        });

        // Ricarichiamo dal DB per verificare l'aggiornamento
        Hackathon h = hackathonRepo.findByNome("HackFinale").get();
        assertTrue(h.isMentore(mentore), "Il mentore deve risultare nello staff dell'Hackathon creato.");
    }
}