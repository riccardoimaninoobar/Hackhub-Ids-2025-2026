package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Hackathon;
import it.unicam.hackhub.domain.model.HackathonBuilder;
import it.unicam.hackhub.domain.model.RichiestaSupporto;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.RichiestaSupportoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GRichiestaSupportoHandlerTest {

    private Sessione sessione;
    private FakeRichiestaSupportoRepository richiestaRepo;
    private FakeHackathonRepository hackathonRepo;
    private FakeCalendarService calendarService;
    private GRichiestaSupportoHandler handler;

    private Utente mentore;
    private Utente utenteNonAutorizzato;
    private Hackathon hackathonMentore;
    private Hackathon hackathonAltro;
    private FakeRichiestaSupporto richiestaApertaMentore;
    private FakeRichiestaSupporto richiestaChiusaMentore;
    private FakeRichiestaSupporto richiestaApertaAltroHackathon;

    @BeforeEach
    void setUp() {
        mentore = new Utente("mentore1", "mentore1@mail.it", "pass");
        utenteNonAutorizzato = new Utente("utente1", "utente1@mail.it", "pass");

        sessione = new Sessione(mentore);
        richiestaRepo = new FakeRichiestaSupportoRepository();
        hackathonRepo = new FakeHackathonRepository();
        calendarService = new FakeCalendarService();

        handler = new GRichiestaSupportoHandler(sessione, richiestaRepo, hackathonRepo, calendarService);

        hackathonMentore = creaHackathon("Hack1", mentore);
        hackathonAltro = creaHackathon("Hack2", new Utente("altroMentore", "altro@mail.it", "pass"));

        richiestaApertaMentore = new FakeRichiestaSupporto(hackathonMentore, true);
        richiestaChiusaMentore = new FakeRichiestaSupporto(hackathonMentore, false);
        richiestaApertaAltroHackathon = new FakeRichiestaSupporto(hackathonAltro, true);

        hackathonRepo.save(hackathonMentore);
        hackathonRepo.save(hackathonAltro);

        richiestaRepo.save(richiestaApertaMentore);
        richiestaRepo.save(richiestaChiusaMentore);
        richiestaRepo.save(richiestaApertaAltroHackathon);
    }

    @Test
    void getRichiesteSupporto_restituisceSoloRichiesteAperteDellHackathonDelMentore() {
        List<RichiestaSupporto> risultato = handler.getRichiesteSupporto();

        assertEquals(1, risultato.size());
        assertTrue(risultato.contains(richiestaApertaMentore));
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
    void getRichiesteSupporto_lanciaEccezione_seNonCiSonoRichiesteDaGestire() {
        richiestaRepo.clear();
        richiestaRepo.save(richiestaChiusaMentore);
        richiestaRepo.save(richiestaApertaAltroHackathon);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> handler.getRichiesteSupporto()
        );

        assertEquals("Non ci sono richieste di supporto da gestire.", ex.getMessage());
    }

    @Test
    void rispondiRichiesta_aggiungeRispostaESalva() {
        handler.rispondiRichiesta(richiestaApertaMentore, "  Risposta del mentore  ");

        assertEquals("Risposta del mentore", richiestaApertaMentore.getRisposta());
        assertTrue(richiestaRepo.findAll().contains(richiestaApertaMentore));
    }

    @Test
    void rispondiRichiesta_lanciaEccezione_seRispostaVuota() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> handler.rispondiRichiesta(richiestaApertaMentore, "   ")
        );

        assertEquals("La risposta non può essere vuota.", ex.getMessage());
    }

    @Test
    void prenotaSlotCalendar_associaSlotESalva_seDisponibile() {
        LocalDate data = LocalDate.of(2026, 5, 10);
        LocalTime ora = LocalTime.of(15, 30);

        calendarService.setDisponibile(true);
        calendarService.setPrenotazioneRiuscita(true);

        handler.prenotaSlotCalendar(richiestaApertaMentore, data, ora);

        assertEquals(data, richiestaApertaMentore.getDataSlot());
        assertEquals(ora, richiestaApertaMentore.getOraSlot());
    }

    @Test
    void prenotaSlotCalendar_lanciaEccezione_seSlotNonDisponibile() {
        LocalDate data = LocalDate.of(2026, 5, 10);
        LocalTime ora = LocalTime.of(15, 30);

        calendarService.setDisponibile(false);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> handler.prenotaSlotCalendar(richiestaApertaMentore, data, ora)
        );

        assertEquals("Slot non disponibile.", ex.getMessage());
    }

    @Test
    void prenotaSlotCalendar_lanciaEccezione_sePrenotazioneFallisce() {
        LocalDate data = LocalDate.of(2026, 5, 10);
        LocalTime ora = LocalTime.of(15, 30);

        calendarService.setDisponibile(true);
        calendarService.setPrenotazioneRiuscita(false);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> handler.prenotaSlotCalendar(richiestaApertaMentore, data, ora)
        );

        assertEquals("Errore durante la prenotazione dello slot.", ex.getMessage());
    }

    @Test
    void gestisciRichiesta_esegueRispostaEPrenotazione() {
        LocalDate data = LocalDate.of(2026, 5, 12);
        LocalTime ora = LocalTime.of(10, 0);

        calendarService.setDisponibile(true);
        calendarService.setPrenotazioneRiuscita(true);

        handler.gestisciRichiesta(richiestaApertaMentore, "Ti rispondo qui", data, ora);

        assertEquals("Ti rispondo qui", richiestaApertaMentore.getRisposta());
        assertEquals(data, richiestaApertaMentore.getDataSlot());
        assertEquals(ora, richiestaApertaMentore.getOraSlot());
    }

    private Hackathon creaHackathon(String nome, Utente mentoreAssociato) {
        Utente organizzatore = new Utente("org_" + nome, nome + "_org@mail.it", "pass");
        Utente giudice = new Utente("giu_" + nome, nome + "_giu@mail.it", "pass");

        return new HackathonBuilder()
                .assegnaNome(nome)
                .assegnaRegolamento("Regolamento " + nome)
                .assegnaScadenza(LocalDate.now().plusDays(5))
                .assegnaDataInizio(LocalDate.now().plusDays(10))
                .assegnaDataFine(LocalDate.now().plusDays(20))
                .assegnaLuogo("Camerino")
                .assegnaDimMaxTeam(5)
                .assegnaOrganizzatore(organizzatore)
                .assegnaGiudice(giudice)
                .assegnaMentore(mentoreAssociato)
                .assegnaPremioImporto(BigDecimal.valueOf(1000))
                .build();
    }

    private static class FakeRichiestaSupportoRepository implements RichiestaSupportoRepository {
        private final List<RichiestaSupporto> archivio = new ArrayList<>();

        @Override
        public List<RichiestaSupporto> findAll() {
            return new ArrayList<>(archivio);
        }

        @Override
        public void save(RichiestaSupporto richiesta) {
            if (!archivio.contains(richiesta)) {
                archivio.add(richiesta);
            }
        }

        @Override
        public Optional<RichiestaSupporto> findById(String id) {
            return archivio.stream()
                    .filter(r -> r.getId().equals(id))
                    .findFirst();
        }

        @Override
        public boolean existsById(String id) {
            return archivio.stream().anyMatch(r -> r.getId().equals(id));
        }

        public void clear() {
            archivio.clear();
        }
    }

    private static class FakeHackathonRepository implements HackathonRepository {
        private final List<Hackathon> archivio = new ArrayList<>();

        @Override
        public List<Hackathon> findAll() {
            return new ArrayList<>(archivio);
        }

        @Override
        public void save(Hackathon hackathon) {
            if (!archivio.contains(hackathon)) {
                archivio.add(hackathon);
            }
        }

        @Override
        public Optional<Hackathon> findById(String id) {
            return archivio.stream()
                    .filter(h -> h.getNome().equals(id))
                    .findFirst();
        }

        @Override
        public boolean existsById(String id) {
            return archivio.stream().anyMatch(h -> h.getNome().equals(id));
        }
    }

    private static class FakeCalendarService implements CalendarService {
        private boolean disponibile = true;
        private boolean prenotazioneRiuscita = true;

        public void setDisponibile(boolean disponibile) {
            this.disponibile = disponibile;
        }

        public void setPrenotazioneRiuscita(boolean prenotazioneRiuscita) {
            this.prenotazioneRiuscita = prenotazioneRiuscita;
        }

        @Override
        public boolean verificaDisponibilita(LocalDate data, LocalTime ora) {
            return disponibile;
        }

        @Override
        public boolean prenotaSlot(LocalDate data, LocalTime ora) {
            return prenotazioneRiuscita;
        }
    }

    private static class FakeRichiestaSupporto extends RichiestaSupporto {
        private boolean aperta;
        private String risposta;
        private LocalDate dataSlot;
        private LocalTime oraSlot;

        public FakeRichiestaSupporto(Hackathon hackathon, boolean aperta) {
            super(null, hackathon, "Descrizione di test");
            this.aperta = aperta;
        }

        public boolean isAperta() {
            return aperta;
        }

        public void aggiungiRisposta(String risposta) {
            this.risposta = risposta;
            this.aperta = false;
        }

        public void associaSlot(LocalDate data, LocalTime ora) {
            this.dataSlot = data;
            this.oraSlot = ora;
        }

        public String getRisposta() {
            return risposta;
        }

        public LocalDate getDataSlot() {
            return dataSlot;
        }

        public LocalTime getOraSlot() {
            return oraSlot;
        }
    }
}