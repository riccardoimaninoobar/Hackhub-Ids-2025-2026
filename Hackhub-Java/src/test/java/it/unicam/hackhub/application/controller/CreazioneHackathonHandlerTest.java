package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Hackathon;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryHackathonRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryUtenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CreazioneHackathonHandlerTest {

    private CreazioneHackathonHandler handler;
    private AggiungiMentoreHandler aggiungiMentoreHandler;

    private Sessione sessione;
    private HackathonRepository hackathonRepo;
    private UtenteRepository utenteRepo;

    private Utente organizzatore;
    private Utente utenteGiudice;
    private Utente mentore;

    @BeforeEach
    void setUp() {
        sessione = new Sessione(null);
        hackathonRepo = new InMemoryHackathonRepository();
        utenteRepo = new InMemoryUtenteRepository();

        aggiungiMentoreHandler = new AggiungiMentoreHandler(hackathonRepo, utenteRepo, sessione);
        handler = new CreazioneHackathonHandler(hackathonRepo, utenteRepo, aggiungiMentoreHandler, sessione);

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

        // Non è ancora salvato nel repo, perché manca il giudice e il build()!
        assertFalse(hackathonRepo.existsById("GlobalHack"));
    }

    @Test
    void creaHackathonBase_FallisceSeGiaEsiste() {
        sessione.setUtenteCorrente(organizzatore);

        // Simuliamo un Hackathon già esistente
        handler.creaHackathonBase("HackEsistente", "R", LocalDate.now(), LocalDate.now(), LocalDate.now().plusDays(1), "L", 2, new BigDecimal("10"));
        handler.assegnaGiudice("giudice"); // Lo salva!

        // Tentiamo di ricrearne un altro con lo stesso nome
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

        // Ora deve essere salvato nel repository
        Optional<Hackathon> salvato = hackathonRepo.findById("Hack1");
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
        handler.assegnaGiudice("giudice"); // Salva l'hackathon nel repo

        // Act: richiamiamo assegnaMentore sul CreazioneHackathonHandler
        assertDoesNotThrow(() -> {
            handler.assegnaMentore("esperto");
        });

        // Assert: Verifichiamo che il mentore sia stato effettivamente aggiunto
        Hackathon h = hackathonRepo.findById("HackFinale").get();
        assertTrue(h.isMentore(mentore), "Il mentore deve risultare nello staff dell'Hackathon creato.");
    }
}