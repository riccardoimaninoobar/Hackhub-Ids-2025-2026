package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Hackathon;
import it.unicam.hackhub.domain.model.HackathonBuilder;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.TeamRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryHackathonRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryTeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class IscrizioneTeamHandlerTest {

    private HackathonRepository hackathonRepository;
    private TeamRepository teamRepository;
    private Sessione sessione;
    private IscrizioneTeamHandler handler;

    private Utente utente;
    private Utente organizzatore;
    private Utente giudice;
    private Utente mentore;
    private Team team;

    @BeforeEach
    void setUp() {
        hackathonRepository = new InMemoryHackathonRepository();
        teamRepository = new InMemoryTeamRepository();
        sessione = new Sessione(null);

        handler = new IscrizioneTeamHandler(hackathonRepository, teamRepository, sessione);

        utente = new Utente("membro1", "membro1@mail.it", "pass");
        organizzatore = new Utente("organizzatore", "org@mail.it", "pass");
        giudice = new Utente("giudice", "giudice@mail.it", "pass");
        mentore = new Utente("mentore", "mentore@mail.it", "pass");

        team = new Team("TeamAlpha");
        team.addMember(utente);

        teamRepository.save(team);
        sessione.setUtenteCorrente(utente);
    }

    @Test
    void getHackathonInIscrizione_restituisceGliHackathonDisponibili() {
        Hackathon h1 = creaHackathonInIscrizione("HackathonAI");
        Hackathon h2 = creaHackathonInIscrizione("HackathonWeb");

        hackathonRepository.save(h1);
        hackathonRepository.save(h2);

        Set<Hackathon> risultato = handler.getHackathonInIscrizione();

        assertNotNull(risultato);
        assertEquals(2, risultato.size());
        assertTrue(risultato.stream().anyMatch(h -> h.getNome().equals("HackathonAI")));
        assertTrue(risultato.stream().anyMatch(h -> h.getNome().equals("HackathonWeb")));
    }

    @Test
    void iscriviTeam_teamDellUtenteVieneIscrittoConSuccesso() {
        Hackathon hackathon = creaHackathonInIscrizione("HackProva");
        hackathonRepository.save(hackathon);

        handler.iscriviTeam(hackathon);

        Hackathon salvato = hackathonRepository.findById("HackProva")
        .orElseThrow(() -> new AssertionError("Hackathon non trovato"));
        assertTrue(salvato.utentePartecipante(utente));
    }

    @Test
    void iscriviTeam_lanciaEccezione_seHackathonNonEPiuInIscrizione() {
        Hackathon hackathon = creaHackathonScaduto("HackScaduto");
        hackathonRepository.save(hackathon);

        assertThrows(RuntimeException.class, () -> handler.iscriviTeam(hackathon));
    }

    private Hackathon creaHackathonInIscrizione(String nome) {
        return new HackathonBuilder()
                .assegnaNome(nome)
                .assegnaRegolamento("Regolamento " + nome)
                .assegnaScadenza(LocalDate.now().plusDays(10))
                .assegnaDataInizio(LocalDate.now().plusDays(20))
                .assegnaDataFine(LocalDate.now().plusDays(30))
                .assegnaLuogo("Camerino")
                .assegnaDimMaxTeam(5)
                .assegnaPremioImporto(new BigDecimal("1000"))
                .assegnaOrganizzatore(organizzatore)
                .assegnaGiudice(giudice)
                .assegnaMentore(mentore)
                .build();
    }

    private Hackathon creaHackathonScaduto(String nome) {
        return new HackathonBuilder()
                .assegnaNome(nome)
                .assegnaRegolamento("Regolamento " + nome)
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
    }
}