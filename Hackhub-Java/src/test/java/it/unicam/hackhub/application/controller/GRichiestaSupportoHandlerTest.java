package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.RichiestaSupporto;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.model.eventi.NotificaEvent;
import it.unicam.hackhub.domain.model.hackathon.Hackathon;
import it.unicam.hackhub.domain.model.hackathon.HackathonBuilder;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.RichiestaSupportoRepository;
import it.unicam.hackhub.domain.repository.TeamRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import it.unicam.hackhub.domain.service.CalendarService;
import it.unicam.hackhub.presentation.CliRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class GRichiestaSupportoHandlerTest {

    @MockitoBean
    private CliRunner cliRunner;

    @MockitoBean
    private CalendarService calendarService;

    @MockitoBean
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private GRichiestaSupportoHandler handler;

    @Autowired
    private Sessione sessione;

    @Autowired
    private RichiestaSupportoRepository richiestaRepo;

    @Autowired
    private HackathonRepository hackathonRepo;

    @Autowired
    private UtenteRepository utenteRepo;

    @Autowired
    private TeamRepository teamRepo;

    private Utente mentore;
    private Utente organizzatore;
    private Utente giudice;
    private Utente utenteNonAutorizzato;
    private Utente membroTeam1;
    private Utente membroTeam2;

    private Team teamAlpha;
    private Hackathon hackathonMentore;
    private RichiestaSupporto richiestaApertaMentore;

    @BeforeEach
    void setUp() {
        mentore = new Utente("mentore1", "mentore1@mail.it", "pass");
        organizzatore = new Utente("organizzatore1", "organizzatore1@mail.it", "pass");
        giudice = new Utente("giudice1", "giudice1@mail.it", "pass");
        utenteNonAutorizzato = new Utente("utenteX", "utenteX@mail.it", "pass");
        membroTeam1 = new Utente("membro1", "membro1@mail.it", "pass");
        membroTeam2 = new Utente("membro2", "membro2@mail.it", "pass");

        utenteRepo.save(mentore);
        utenteRepo.save(organizzatore);
        utenteRepo.save(giudice);
        utenteRepo.save(utenteNonAutorizzato);
        utenteRepo.save(membroTeam1);
        utenteRepo.save(membroTeam2);

        teamAlpha = new Team("TeamAlpha");
        teamAlpha.aggiungiMembro(membroTeam1);
        teamAlpha.aggiungiMembro(membroTeam2);
        teamRepo.save(teamAlpha);

        hackathonMentore = new HackathonBuilder()
                .assegnaNome("HackathonMentore")
                .assegnaRegolamento("Regolamento di prova")
                .assegnaScadenza(LocalDate.now().minusDays(10))
                .assegnaDataInizio(LocalDate.now().minusDays(5))
                .assegnaDataFine(LocalDate.now().plusDays(5))
                .assegnaLuogo("Camerino")
                .assegnaDimMaxTeam(5)
                .assegnaPremioImporto(new BigDecimal("1000"))
                .assegnaOrganizzatore(organizzatore)
                .assegnaGiudice(giudice)
                .assegnaMentore(mentore)
                .build();

        hackathonMentore.aggiornaStato();
        hackathonRepo.save(hackathonMentore);

        richiestaApertaMentore = new RichiestaSupporto(
                teamAlpha,
                hackathonMentore,
                "Descrizione del problema"
        );
        richiestaRepo.save(richiestaApertaMentore);

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

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> handler.getRichiesteSupporto()
        );

        assertEquals("Devi effettuare il login.", ex.getMessage());
    }

    @Test
    void getRichiesteSupporto_lanciaEccezione_seUtenteNonEMentore() {
        sessione.setUtenteCorrente(utenteNonAutorizzato);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> handler.getRichiesteSupporto()
        );

        assertEquals("Non sei autorizzato a gestire richieste di supporto.", ex.getMessage());
    }

    @Test
    void rispondiRichiesta_aggiungeRispostaESalva() {
        handler.rispondiRichiesta(richiestaApertaMentore, " Risposta del mentore ");

        RichiestaSupporto salvata = richiestaRepo.findById(richiestaApertaMentore.getId()).orElseThrow();

        assertEquals("Risposta del mentore", salvata.getRisposta());
    }

    @Test
    void prenotaSlotCalendar_associaSlotESalva_seDisponibile() {
        LocalDate data = LocalDate.of(2026, 5, 10);
        LocalTime ora = LocalTime.of(15, 30);

        when(calendarService.verificaDisponibilita(data, ora)).thenReturn(true);
        when(calendarService.prenotaSlot(data, ora)).thenReturn(true);

        handler.prenotaSlotCalendar(richiestaApertaMentore, data, ora);

        RichiestaSupporto salvata = richiestaRepo.findById(richiestaApertaMentore.getId()).orElseThrow();

        assertEquals(data, salvata.getDataCall());
        assertEquals(ora, salvata.getOraCall());
    }

    @Test
    void prenotaSlotCalendar_lanciaEccezione_seSlotNonDisponibile() {
        LocalDate data = LocalDate.of(2026, 5, 10);
        LocalTime ora = LocalTime.of(15, 30);

        when(calendarService.verificaDisponibilita(data, ora)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> handler.prenotaSlotCalendar(richiestaApertaMentore, data, ora)
        );

        assertEquals("Slot non disponibile.", ex.getMessage());
    }

    @Test
    void gestisciRichiesta_pubblicaUnaNotificaPerOgniMembroDelTeam() {
        LocalDate data = LocalDate.of(2026, 5, 10);
        LocalTime ora = LocalTime.of(15, 30);

        when(calendarService.verificaDisponibilita(data, ora)).thenReturn(true);
        when(calendarService.prenotaSlot(data, ora)).thenReturn(true);

        handler.gestisciRichiesta(richiestaApertaMentore, "Risposta del mentore", data, ora);

        ArgumentCaptor<NotificaEvent> captor = ArgumentCaptor.forClass(NotificaEvent.class);
        verify(eventPublisher, times(2)).publishEvent(captor.capture());

        List<NotificaEvent> eventi = captor.getAllValues();
        assertEquals(2, eventi.size());

        assertTrue(eventi.stream().anyMatch(e ->
                e.destinatario().equals(membroTeam1) &&
                e.titolo().equals("Richiesta di supporto gestita") &&
                e.messaggio().contains("Risposta del mentore")
        ));

        assertTrue(eventi.stream().anyMatch(e ->
                e.destinatario().equals(membroTeam2) &&
                e.titolo().equals("Richiesta di supporto gestita") &&
                e.messaggio().contains("Risposta del mentore")
        ));
    }

    @Test
    void gestisciRichiesta_salvaRispostaESlotEChiudeLaRichiesta() {
        LocalDate data = LocalDate.of(2026, 5, 10);
        LocalTime ora = LocalTime.of(15, 30);

        when(calendarService.verificaDisponibilita(data, ora)).thenReturn(true);
        when(calendarService.prenotaSlot(data, ora)).thenReturn(true);

        handler.gestisciRichiesta(richiestaApertaMentore, "Risposta finale", data, ora);

        RichiestaSupporto salvata = richiestaRepo.findById(richiestaApertaMentore.getId()).orElseThrow();

        assertEquals("Risposta finale", salvata.getRisposta());
        assertEquals(data, salvata.getDataCall());
        assertEquals(ora, salvata.getOraCall());
        assertFalse(salvata.isAperta());
    }
}