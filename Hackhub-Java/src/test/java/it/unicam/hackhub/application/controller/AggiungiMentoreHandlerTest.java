package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Hackathon;
import it.unicam.hackhub.domain.model.HackathonBuilder;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryHackathonRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryUtenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AggiungiMentoreHandlerTest {

    private AggiungiMentoreHandler handler;
    private Sessione sessione;
    private HackathonRepository hackathonRepo;
    private UtenteRepository utenteRepo;

    private Utente organizzatore;
    private Utente utenteGiudice;
    private Utente candidatoMentore;
    private Utente partecipante;
    private Hackathon hackathonTest;

    @BeforeEach
    void setUp() {
        sessione = new Sessione(null);
        hackathonRepo = new InMemoryHackathonRepository();
        utenteRepo = new InMemoryUtenteRepository();
        handler = new AggiungiMentoreHandler(hackathonRepo, utenteRepo, sessione);

        // Prepariamo gli utenti
        organizzatore = new Utente("org", "org@mail.it", "pass");
        utenteGiudice = new Utente("giudice", "g@mail.it", "pass");
        candidatoMentore = new Utente("esperto", "esp@mail.it", "pass");
        partecipante = new Utente("player1", "p1@mail.it", "pass");

        utenteRepo.save(organizzatore);
        utenteRepo.save(utenteGiudice);
        utenteRepo.save(candidatoMentore);
        utenteRepo.save(partecipante);

        hackathonTest = new HackathonBuilder()
                .assegnaNome("HackTest")
                .assegnaOrganizzatore(organizzatore)
                .assegnaGiudice(utenteGiudice)
                .build();

        Team team = new Team("Team Beta");
        team.addMember(partecipante);
        hackathonTest.aggiungiTeam(team);

        hackathonRepo.save(hackathonTest);
    }

    // ==========================================================
    // TEST 1: Metodo checkOrg
    // ==========================================================

    @Test
    void checkOrg_SuccessoSeUtenteLoggatoEOrganizzatore() {
        sessione.setUtenteCorrente(organizzatore);
        assertDoesNotThrow(() -> handler.checkOrg("HackTest"));
    }

    @Test
    void checkOrg_FallisceSeUtenteNonLoggato() {
        sessione.setUtenteCorrente(null);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> handler.checkOrg("HackTest"));
        assertEquals("Devi effettuare il login per eseguire questa azione.", ex.getMessage());
    }

    @Test
    void checkOrg_FallisceSeNonOrganizzatore() {
        // Un utente qualsiasi tenta di aggiungere un mentore
        sessione.setUtenteCorrente(candidatoMentore);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> handler.checkOrg("HackTest"));
        assertEquals("L'utente loggato non è Organizzatore dell'Hackathon", ex.getMessage());
    }

    // ==========================================================
    // TEST 2: Metodo aggiungiMentore
    // ==========================================================

    @Test
    void aggiungiMentore_Successo() {
        // Arrange
        sessione.setUtenteCorrente(organizzatore);
        handler.checkOrg("HackTest"); // Necessario per impostare lo stato interno dell'handler

        // Act
        handler.aggiungiMentore("esperto");

        // Assert
        assertTrue(hackathonTest.isMentore(candidatoMentore));
    }

    @Test
    void aggiungiMentore_FallisceSeGiaNelloStaff() {
        sessione.setUtenteCorrente(organizzatore);
        handler.checkOrg("HackTest");

        // Tenta di aggiungere il giudice come mentore
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> handler.aggiungiMentore("giudice"));
        assertEquals("Utente già parte dello staff", ex.getMessage());
    }

    @Test
    void aggiungiMentore_FallisceSeUtentePartecipante() {
        sessione.setUtenteCorrente(organizzatore);
        handler.checkOrg("HackTest");

        // Tenta di aggiungere un partecipante
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> handler.aggiungiMentore("player1"));
        assertEquals("Utente partecipante", ex.getMessage());
    }
}