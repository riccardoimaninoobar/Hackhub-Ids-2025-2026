package it.unicam.hackhub.application.controller;
import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.hackathon.Hackathon;
import it.unicam.hackhub.domain.model.hackathon.HackathonBuilder;
import it.unicam.hackhub.domain.model.Sottomissione;
import it.unicam.hackhub.domain.model.hackathon.state.StatoInCorso;
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
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class ConsultareSottomissioniHandlerTest {
    @MockitoBean
    private CliRunner cliRunner;
    @Autowired
    private ConsultareSottomissioniHandler handler;
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
                .assegnaNome("HackTest")
                .assegnaRegolamento("Regole test")
                .assegnaScadenza(LocalDate.now().minusDays(10))
                .assegnaDataInizio(LocalDate.now().minusDays(5))
                .assegnaDataFine(LocalDate.now().plusDays(5))
                .assegnaLuogo("Camerino")
                .assegnaDimMaxTeam(5)
                .assegnaPremioImporto(new BigDecimal("1000"))
                .assegnaOrganizzatore(organizzatore)
                .assegnaGiudice(giudice)
                .build();
        hackathon.setStato(new StatoInCorso());
        hackathon.aggiungiTeam(team);
        Sottomissione s1 = new Sottomissione("progettoA.zip", "/files/progettoA.zip", team);
        Sottomissione s2 = new Sottomissione("progettoB.zip", "/files/progettoB.zip", team);
        hackathon.aggiungiSottomissione(s1);
        hackathon.aggiungiSottomissione(s2);
        hackathonRepo.save(hackathon);
    }
    @Test
    void getSottomissioniHackathon_RitornaSottomissioniPerGiudice() {
        sessione.setUtenteCorrente(giudice);
        List<Sottomissione> risultato = handler.getSottomissioniHackathon("HackTest");
        assertEquals(2, risultato.size());
        assertEquals("progettoA.zip", risultato.get(0).getNomeFile());
        assertEquals("progettoB.zip", risultato.get(1).getNomeFile());
    }
    @Test
    void getSottomissioniHackathon_RitornaSottomissioniPerOrganizzatore() {
        sessione.setUtenteCorrente(organizzatore);
        List<Sottomissione> risultato = handler.getSottomissioniHackathon("HackTest");
        assertEquals(2, risultato.size());
        assertFalse(risultato.isEmpty());
    }
    @Test
    void getSottomissioniHackathon_FallisceSeUtenteNonLoggato() {
        sessione.setUtenteCorrente(null);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            handler.getSottomissioniHackathon("HackTest");
        });
        assertEquals("Devi effettuare il login per consultare le sottomissioni.", ex.getMessage());
    }
    @Test
    void getSottomissioniHackathon_FallisceSeHackathonNonEsiste() {
        sessione.setUtenteCorrente(giudice);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            handler.getSottomissioniHackathon("HackathonInesistente");
        });
        assertEquals("Hackathon non trovato.", ex.getMessage());
    }
    @Test
    void getSottomissioniHackathon_FallisceSeUtenteNonAutorizzato() {
        sessione.setUtenteCorrente(esterno);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            handler.getSottomissioniHackathon("HackTest");
        });
        assertEquals("Non sei autorizzato a consultare le sottomissioni di questo Hackathon.", ex.getMessage());
    }
}