package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.*;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.RichiestaSupportoRepository;
import it.unicam.hackhub.domain.repository.TeamRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import it.unicam.hackhub.domain.service.CalendarService;
import it.unicam.hackhub.presentation.CliRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class GRichiestaSupportoHandlerTest {

    @MockitoBean
    private CliRunner cliRunner;

    @Autowired private GRichiestaSupportoHandler handler;
    @Autowired private Sessione sessione;

    // Usiamo i VERI repository di Spring Data JPA
    @Autowired private RichiestaSupportoRepository richiestaRepo;
    @Autowired private HackathonRepository hackathonRepo;
    @Autowired private UtenteRepository utenteRepo;
    @Autowired private TeamRepository teamRepo;

    // Questa è una MAGIA di Spring: "Crea un finto CalendarService e iniettalo nell'Handler al posto di quello vero"
    @MockitoBean private CalendarService calendarService;

    private Utente mentore;
    private Utente utenteNonAutorizzato;
    private Hackathon hackathonMentore;
    private RichiestaSupporto richiestaApertaMentore;

    @BeforeEach
    void setUp() {
        // 1. Creiamo e salviamo gli utenti
        mentore = new Utente("mentore1", "mentore1@mail.it", "pass");
        utenteNonAutorizzato = new Utente("utente1", "utente1@mail.it", "pass");
        utenteRepo.save(mentore);
        utenteRepo.save(utenteNonAutorizzato);

        // 2. Creiamo e salviamo un Team (obbligatorio per la RichiestaSupporto vera)
        Team teamAlpha = new Team("Team Alpha");
        teamRepo.save(teamAlpha);

        // 3. Creiamo e salviamo gli Hackathon usando il tuo vero Builder
        Utente org = new Utente("org", "org@mail.it", "pass");
        Utente giudice = new Utente("giu", "giu@mail.it", "pass");
        utenteRepo.save(org);
        utenteRepo.save(giudice);

        hackathonMentore = new HackathonBuilder()
                .assegnaNome("HackMentore")
                .assegnaRegolamento("Regole")
                .assegnaScadenza(LocalDate.now().plusDays(5))
                .assegnaDataInizio(LocalDate.now().plusDays(10))
                .assegnaDataFine(LocalDate.now().plusDays(20))
                .assegnaLuogo("Camerino")
                .assegnaDimMaxTeam(5)
                .assegnaOrganizzatore(org)
                .assegnaGiudice(giudice)
                .assegnaMentore(mentore) // Assegniamo il mentore!
                .assegnaPremioImporto(BigDecimal.valueOf(1000))
                .build();
        hackathonRepo.save(hackathonMentore);

        // 4. Creiamo e salviamo una VERA Richiesta di Supporto
        richiestaApertaMentore = new RichiestaSupporto(teamAlpha, hackathonMentore, "Descrizione del problema");
        richiestaRepo.save(richiestaApertaMentore);

        // Impostiamo la sessione per i test
        sessione.setUtenteCorrente(mentore);
    }

    @Test
    void getRichiesteSupporto_restituisceSoloRichiesteAperteDellHackathonDelMentore() {
        List<RichiestaSupporto> risultato = handler.getRichiesteSupporto();

        assertEquals(1, risultato.size());
        assertEquals("Descrizione del problema", risultato.get(0).getDescrizione());
    }

    @Test
    void getRichiesteSupporto_lanciaEccezione_seUtenteNonLoggato() {
        sessione.setUtenteCorrente(null);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> handler.getRichiesteSupporto());
        assertEquals("Devi effettuare il login.", ex.getMessage());
    }

    @Test
    void getRichiesteSupporto_lanciaEccezione_seUtenteNonEMentore() {
        sessione.setUtenteCorrente(utenteNonAutorizzato);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> handler.getRichiesteSupporto());
        assertEquals("Non sei autorizzato a gestire richieste di supporto.", ex.getMessage());
    }

    @Test
    void rispondiRichiesta_aggiungeRispostaESalva() {
        handler.rispondiRichiesta(richiestaApertaMentore, "  Risposta del mentore  ");

        // Ricarichiamo dal DB per essere sicuri che abbia salvato!
        RichiestaSupporto salvata = richiestaRepo.findById(richiestaApertaMentore.getId()).get();
        assertEquals("Risposta del mentore", salvata.getRisposta());
    }

    @Test
    void prenotaSlotCalendar_associaSlotESalva_seDisponibile() {
        LocalDate data = LocalDate.of(2026, 5, 10);
        LocalTime ora = LocalTime.of(15, 30);

        // Istruiamo il finto CalendarService (Mock)
        Mockito.when(calendarService.verificaDisponibilita(data, ora)).thenReturn(true);
        Mockito.when(calendarService.prenotaSlot(data, ora)).thenReturn(true);

        handler.prenotaSlotCalendar(richiestaApertaMentore, data, ora);

        // Verifichiamo che i dati siano stati applicati alla richiesta
        RichiestaSupporto salvata = richiestaRepo.findById(richiestaApertaMentore.getId()).get();
        assertEquals(data, salvata.getDataCall());
        assertEquals(ora, salvata.getOraCall());
    }

    @Test
    void prenotaSlotCalendar_lanciaEccezione_seSlotNonDisponibile() {
        LocalDate data = LocalDate.of(2026, 5, 10);
        LocalTime ora = LocalTime.of(15, 30);

        Mockito.when(calendarService.verificaDisponibilita(data, ora)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> handler.prenotaSlotCalendar(richiestaApertaMentore, data, ora));
        assertEquals("Slot non disponibile.", ex.getMessage());
    }
}