package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.*;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryHackathonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CaricaSottomissioneHandlerTest {

    private CaricaSottomissioneHandler handler;
    private Sessione sessione;
    private HackathonRepository hackathonRepo;

    private Utente utenteTest;
    private Team teamTest;
    private Hackathon hackathonInCorso;

    // Nel setUp() di CaricaSottomissioneHandlerTest.java
    @BeforeEach
    void setUp() {
        sessione = new Sessione(null);
        hackathonRepo = new InMemoryHackathonRepository();
        handler = new CaricaSottomissioneHandler(hackathonRepo, sessione);

        utenteTest = new Utente("devUser", "dev@mail.com", "pass");
        teamTest = new Team("Coders");
        teamTest.addMember(utenteTest);

        hackathonInCorso = new HackathonBuilder()
                .assegnaNome("Global Game Jam")
                .assegnaOrganizzatore(new Utente("org", "o@m.it", "p"))
                .assegnaScadenza(LocalDate.now().plusDays(1)) // Scadenza domani
                .assegnaDataFine(LocalDate.now().plusDays(2))     // Fine tra due giorni
                .build();

        // Forza lo stato iniziale e iscrivi il team
        hackathonInCorso.setStato(new StatoInCorso());
        hackathonInCorso.aggiungiTeam(teamTest);

        hackathonRepo.save(hackathonInCorso);
    }

    @Test
    void caricaSottomissione_Successo() {
        // Arrange
        sessione.setUtenteCorrente(utenteTest);
        String link = "https://github.com/user/repo";

        // Act
        assertDoesNotThrow(() -> handler.caricaSottomissione(hackathonInCorso, link));

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
        assertTrue(hs.contains(hackathonInCorso));
    }
}