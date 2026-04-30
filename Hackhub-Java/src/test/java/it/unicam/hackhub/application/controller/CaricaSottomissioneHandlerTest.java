package it.unicam.hackhub.application.controller;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import it.unicam.hackhub.presentation.CliRunner;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.*;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.TeamRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CaricaSottomissioneHandlerTest {

    // Disabilita l'interfaccia a riga di comando durante il test
    @MockitoBean
    private CliRunner cliRunner;

    @Autowired
    private HackathonRepository hackathonRepo;

    @Autowired
    private UtenteRepository utenteRepo;

    @Autowired
    private TeamRepository teamRepo;

    private CaricaSottomissioneHandler handler;
    private Sessione sessione;

    private Utente utenteTest;
    private Utente organizzatore;
    private Team teamTest;
    private Hackathon hackathonInCorso;

    @BeforeEach
    void setUp() {
        sessione = new Sessione();
        handler = new CaricaSottomissioneHandler(hackathonRepo, sessione, utenteRepo);

        // 1. Creiamo e salviamo gli utenti
        utenteTest = new Utente("devUser", "dev@mail.com", "pass");
        organizzatore = new Utente("org", "o@m.it", "p");
        utenteRepo.save(utenteTest);
        utenteRepo.save(organizzatore);

        // 2. Creiamo e salviamo il team
        teamTest = new Team("Coders");
        teamRepo.save(teamTest);

        // 3. Colleghiamo l'utente al team e aggiorniamo il DB
        teamTest.aggiungiMembro(utenteTest);
        utenteRepo.save(utenteTest);

        // 4. Creiamo l'Hackathon
        hackathonInCorso = new HackathonBuilder()
                .assegnaNome("Global Game Jam")
                .assegnaOrganizzatore(organizzatore)
                .assegnaScadenza(LocalDate.now().plusDays(1)) // Scadenza domani
                .assegnaDataFine(LocalDate.now().plusDays(2)) // Fine tra due giorni
                .build();

        // Forza lo stato iniziale e iscrivi il team
        hackathonInCorso.setStato(new StatoInCorso());
        hackathonInCorso.aggiungiTeam(teamTest);

        // Salviamo l'Hackathon nel DB
        hackathonRepo.save(hackathonInCorso);
    }

    @Test
    void caricaSottomissione_Successo() {
        // Arrange
        sessione.setUtenteCorrente(utenteTest);
        String link = "https://github.com/user/repo";

        // Act
        assertDoesNotThrow(() -> handler.caricaSottomissione(hackathonInCorso, link));

        // Sincronizziamo col DB per essere certi che la sottomissione sia persistita
        hackathonRepo.flush();

        // Assert
        assertEquals(1, hackathonInCorso.getSottomissioni().size());
        Sottomissione s = hackathonInCorso.getSottomissioni().iterator().next();
        assertEquals(link, s.getLink());
        assertEquals(teamTest, s.getTeam());
    }

    @Test
    void caricaSottomissione_FallisceSeStatoSbagliato() {
        // Arrange
        sessione.setUtenteCorrente(utenteTest);

        // Cambiamo lo stato in "Valutazione" (dove non si può più caricare)
        hackathonInCorso.setStato(new StatoInValutazione());
        hackathonRepo.save(hackathonInCorso); // Sincronizziamo il cambio di stato

        // Act & Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                handler.caricaSottomissione(hackathonInCorso, "link")
        );
        // L'errore ora viene dal Pattern State (StatoHackathon.java)
        assertTrue(ex.getMessage().contains("non è attualmente in corso"));
    }

    @Test
    void getHackathonInCorso_Successo() {
        // Arrange
        sessione.setUtenteCorrente(utenteTest);

        // Act
        Set<Hackathon> hs = handler.getHackathonInCorso();

        // Assert
        // Controlliamo che la lista recuperata dal DB contenga un Hackathon con il nome corretto
        assertTrue(hs.stream().anyMatch(h -> h.getNome().equals("Global Game Jam")));
    }
}