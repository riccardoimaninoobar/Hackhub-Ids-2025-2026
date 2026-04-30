package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.*;
import it.unicam.hackhub.domain.repository.*;
import it.unicam.hackhub.presentation.CliRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RichiestaSupportoHandlerTest {

    @MockitoBean
    private CliRunner cliRunner;

    @Autowired
    private RichiestaSupportoHandler handler;

    @Autowired
    private Sessione sessione;

    @Autowired
    private UtenteRepository utenteRepo;

    @Autowired
    private TeamRepository teamRepo;

    @Autowired
    private HackathonRepository hackathonRepo;

    @Autowired
    private RichiestaSupportoRepository richiestaRepo;

    private Utente utenteLoggato;
    private Team teamTest;
    private Hackathon hackathonInCorso;

    @BeforeEach
    void setUp() {
        // 1. Creiamo e salviamo l'utente
        utenteLoggato = new Utente("devUser", "dev@mail.com", "pass");
        utenteRepo.save(utenteLoggato);

        // 2. Creiamo e salviamo il team
        teamTest = new Team("AlphaTeam");
        teamRepo.save(teamTest);

        // 3. Colleghiamo utente e team
        teamTest.aggiungiMembro(utenteLoggato);
        utenteRepo.save(utenteLoggato);

        // 4. Creiamo un hackathon in corso e iscriviamo il team
        hackathonInCorso = new HackathonBuilder()
                .assegnaNome("HackInCorso")
                .assegnaOrganizzatore(new Utente("org", "o@m.it", "p")) // L'organizzatore viene salvato a cascata se configurato, o salvato prima
                .assegnaScadenza(LocalDate.now().plusDays(1))
                .assegnaDataInizio(LocalDate.now().minusDays(1))
                .assegnaDataFine(LocalDate.now().plusDays(2))
                .build();

        // Assicuriamoci che l'organizzatore fittizio sia nel DB
        utenteRepo.save(hackathonInCorso.getOrganizzatore());

        hackathonInCorso.setStato(new StatoInCorso());
        hackathonInCorso.aggiungiTeam(teamTest);
        hackathonRepo.save(hackathonInCorso);

        // Prepariamo la sessione
        sessione.setUtenteCorrente(utenteLoggato);
    }

    @Test
    void getHackathons_Successo() {
        Set<Hackathon> hs = handler.getHackathons();

        assertFalse(hs.isEmpty());
        assertTrue(hs.stream().anyMatch(h -> h.getNome().equals("HackInCorso")));
    }

    @Test
    void getHackathons_FallisceSeUtenteNonLoggato() {
        sessione.setUtenteCorrente(null);

        assertThrows(IllegalStateException.class, () -> handler.getHackathons());
    }

    @Test
    void convalidaDescrizione_LanciaEccezioneSeTroppoCorta() {
        String descCorta = "Corta";
        assertThrows(IllegalArgumentException.class, () -> handler.convalidaDescrizione(descCorta));
    }

    @Test
    void convalidaDescrizione_SuccessoSeLungaAbbastanza() {
        String descValida = "Questa descrizione è lunga abbastanza per superare la validazione.";
        assertDoesNotThrow(() -> handler.convalidaDescrizione(descValida));
    }

    @Test
    void registraRichiestaSupporto_SalvaCorrettamenteNelDB() {
        String desc = "Richiesta di supporto tecnico per configurazione ambiente.";

        handler.registraRichiestaSupporto(hackathonInCorso, desc);

        // Verifichiamo la persistenza interrogando la repository
        long count = richiestaRepo.count();
        assertEquals(1, count, "Dovrebbe esserci una richiesta di supporto salvata nel DB");

        RichiestaSupporto salvata = richiestaRepo.findAll().get(0);
        assertEquals(desc, salvata.getDescrizione());
        assertEquals(teamTest.getNome(), salvata.getTeam().getNome());
        assertEquals(hackathonInCorso.getNome(), salvata.getHackathon().getNome());
    }
}