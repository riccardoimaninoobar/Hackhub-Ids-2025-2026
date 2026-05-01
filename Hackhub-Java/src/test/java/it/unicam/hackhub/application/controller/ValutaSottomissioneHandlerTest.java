package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.hackathon.Hackathon;
import it.unicam.hackhub.domain.model.hackathon.HackathonBuilder;
import it.unicam.hackhub.domain.model.Sottomissione;
import it.unicam.hackhub.domain.model.hackathon.state.StatoInCorso;
import it.unicam.hackhub.domain.model.hackathon.state.StatoInValutazione;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.TeamRepository;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ValutaSottomissioneHandlerTest {

    @MockitoBean
    private CliRunner cliRunner;

    @Autowired
    private ValutareSottomissioneHandler handler;

    @Autowired
    private Sessione sessione;

    @Autowired
    private HackathonRepository hackathonRepo;

    @Autowired
    private UtenteRepository utenteRepo;

    @Autowired
    private TeamRepository teamRepo;

    private Utente organizzatore;
    private Utente giudice;
    private Utente esterno;
    private Team team;
    private Hackathon hackathon;
    private Sottomissione sottomissione;

    @BeforeEach
    void setUp() {
        organizzatore = new Utente("org", "org@mail.it", "pass");
        giudice = new Utente("judge", "judge@mail.it", "pass");
        esterno = new Utente("outsider", "out@mail.it", "pass");

        utenteRepo.save(organizzatore);
        utenteRepo.save(giudice);
        utenteRepo.save(esterno);

        team = new Team("TeamAlpha");
        team.setDatiBancari("IT60X0542811101000000123456");
        teamRepo.save(team);

        hackathon = new HackathonBuilder()
                .assegnaNome("HackVal")
                .assegnaRegolamento("Regole test")
                .assegnaScadenza(LocalDate.now().minusDays(20))
                .assegnaDataInizio(LocalDate.now().minusDays(10))
                .assegnaDataFine(LocalDate.now().minusDays(1))
                .assegnaLuogo("Camerino")
                .assegnaDimMaxTeam(5)
                .assegnaPremioImporto(new BigDecimal("1000"))
                .assegnaOrganizzatore(organizzatore)
                .assegnaGiudice(giudice)
                .build();

        hackathon.setStato(new StatoInValutazione());
        hackathon.aggiungiTeam(team);

        sottomissione = new Sottomissione("progetto.zip", "/files/progetto.zip", team);
        hackathon.aggiungiSottomissione(sottomissione);
        hackathonRepo.save(hackathon);
    }

    @Test
    void getHackathonsGiudice_RitornaHackathonDaValutare() {
        sessione.setUtenteCorrente(giudice);

        Set<Hackathon> risultato = handler.getHackathonsGiudice();

        assertEquals(1, risultato.size());
        assertTrue(risultato.stream().anyMatch(h -> h.getNome().equals("HackVal")));
    }

    @Test
    void getHackathonsGiudice_FallisceSeUtenteNonLoggato() {
        sessione.setUtenteCorrente(null);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            handler.getHackathonsGiudice();
        });
        assertEquals("Devi effettuare il login.", ex.getMessage());
    }

    @Test
    void getHackathonsGiudice_FallisceSeNessunHackathonDaValutare() {
        sessione.setUtenteCorrente(esterno);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            handler.getHackathonsGiudice();
        });
        assertEquals("Al momento non hai sottomissioni da valutare.", ex.getMessage());
    }

    @Test
    void getSottomissioni_RitornaSottomissioniHackathon() {
        sessione.setUtenteCorrente(giudice);

        Set<Sottomissione> risultato = handler.getSottomissioni("HackVal");

        assertEquals(1, risultato.size());
        assertEquals("progetto.zip", risultato.iterator().next().getNomeFile());
    }

    @Test
    void getSottomissioni_FallisceSeHackathonInesistente() {
        sessione.setUtenteCorrente(giudice);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            handler.getSottomissioni("HackathonFake");
        });
        assertEquals("Hackathon inesistente.", ex.getMessage());
    }

    @Test
    void getSottomissioni_FallisceSeUtenteNonAutorizzato() {
        sessione.setUtenteCorrente(esterno);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            handler.getSottomissioni("HackVal");
        });
        assertEquals("Non sei autorizzato a valutare le sottomissioni di questo Hackathon.", ex.getMessage());
    }

    @Test
    void getSottomissioni_FallisceSeHackathonNonInValutazione() {
        sessione.setUtenteCorrente(giudice);

        // Creiamo un Hackathon temporaneo con dataFine nel FUTURO per questo test,
        // così aggiornaStato() non lo promuoverà automaticamente a "In Valutazione".
        Hackathon hInCorso = new HackathonBuilder()
                .assegnaNome("HackNonValutabile")
                .assegnaOrganizzatore(organizzatore)
                .assegnaGiudice(giudice)
                .assegnaScadenza(LocalDate.now().minusDays(1))
                .assegnaDataInizio(LocalDate.now().minusDays(1))
                .assegnaDataFine(LocalDate.now().plusDays(10)) // <--- Nel futuro!
                .build();

        hInCorso.setStato(new StatoInCorso());
        hackathonRepo.save(hInCorso);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            handler.getSottomissioni("HackNonValutabile");
        });

        assertEquals("L'Hackathon non è in stato di valutazione.", ex.getMessage());
    }

    @Test
    void assegnaPunteggio_SalvaCorrettamenteIlVoto() {
        sessione.setUtenteCorrente(giudice);

        handler.assegnaPunteggio("HackVal", sottomissione.getId(), 8);

        Hackathon aggiornato = hackathonRepo.findByNome("HackVal").orElseThrow();
        Sottomissione trovata = aggiornato.getSottomissioni().stream()
                .filter(s -> s.getId().equals(sottomissione.getId()))
                .findFirst()
                .orElseThrow();

        assertEquals(8, trovata.getPunteggio());
    }

    @Test
    void assegnaPunteggio_FallisceSeVotoNonValido() {
        sessione.setUtenteCorrente(giudice);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            handler.assegnaPunteggio("HackVal", sottomissione.getId(), 11);
        });
        assertEquals("Votazione non valida, deve essere un numero compreso tra 1 e 10.", ex.getMessage());
    }

    @Test
    void assegnaPunteggio_FallisceSeUtenteNonAutorizzato() {
        sessione.setUtenteCorrente(esterno);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            handler.assegnaPunteggio("HackVal", sottomissione.getId(), 7);
        });
        assertEquals("Non sei autorizzato a valutare questa sottomissione.", ex.getMessage());
    }

    @Test
    void assegnaPunteggio_FallisceSeSottomissioneNonEsiste() {
        sessione.setUtenteCorrente(giudice);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            handler.assegnaPunteggio("HackVal", 99999L, 7);
        });
        assertEquals("Sottomissione non trovata per questo Hackathon.", ex.getMessage());
    }

    @Test
    void assegnaPunteggio_FallisceSeHackathonNonInValutazione() {
        sessione.setUtenteCorrente(giudice);

        // Stessa strategia: un Hackathon specifico con date nel futuro
        Hackathon hInCorso = new HackathonBuilder()
                .assegnaNome("HackNonVotabile")
                .assegnaOrganizzatore(organizzatore)
                .assegnaGiudice(giudice)
                .assegnaScadenza(LocalDate.now().minusDays(1))
                .assegnaDataInizio(LocalDate.now().minusDays(1))
                .assegnaDataFine(LocalDate.now().plusDays(10)) // <--- Nel futuro!
                .build();

        hInCorso.setStato(new StatoInCorso());
        hInCorso.aggiungiTeam(team);

        Sottomissione s = new Sottomissione("progetto_bloccato.zip", "/files/progetto_bloccato.zip", team);
        hInCorso.aggiungiSottomissione(s);
        hackathonRepo.save(hInCorso);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            handler.assegnaPunteggio("HackNonVotabile", s.getId(), 7);
        });

        assertEquals("L'Hackathon non è in stato di valutazione.", ex.getMessage());
    }
}