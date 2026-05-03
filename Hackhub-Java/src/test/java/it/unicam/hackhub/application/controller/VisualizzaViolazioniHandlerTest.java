package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.EsitoSegnalazione;
import it.unicam.hackhub.domain.model.SegnalazioneViolazione;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.model.hackathon.Hackathon;
import it.unicam.hackhub.domain.model.hackathon.HackathonBuilder;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.SegnalazioneRepository;
import it.unicam.hackhub.domain.repository.TeamRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import it.unicam.hackhub.presentation.dto.SegnalazioneResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class VisualizzaViolazioniHandlerTest {



    @Autowired
    private VisualizzaViolazioniHandler handler;

    @Autowired
    private Sessione sessione;

    @Autowired
    private UtenteRepository utenteRepo;

    @Autowired
    private TeamRepository teamRepo;

    @Autowired
    private HackathonRepository hackathonRepo;

    @Autowired
    private SegnalazioneRepository segnalazioneRepo;

    private Utente organizzatore;
    private Utente altroOrganizzatore;

    @BeforeEach
    void setUp() {
        sessione.setUtenteCorrente(null);

        organizzatore = new Utente("org1", "org1@mail.com", "pass");
        altroOrganizzatore = new Utente("org2", "org2@mail.com", "pass");
        Utente mentore = new Utente("mentore1", "m1@mail.com", "pass");

        utenteRepo.save(organizzatore);
        utenteRepo.save(altroOrganizzatore);
        utenteRepo.save(mentore);

        Team teamAlpha = new Team("Team Alpha");
        teamRepo.save(teamAlpha);

        // Hackathon dell'organizzatore principale
        Hackathon hack1 = new HackathonBuilder()
                .assegnaNome("Hack 1")
                .assegnaOrganizzatore(organizzatore)
                .build();
        hack1.aggiungiTeam(teamAlpha);
        hackathonRepo.save(hack1);

        // Hackathon di un ALTRO organizzatore
        Hackathon hack2 = new HackathonBuilder()
                .assegnaNome("Hack 2")
                .assegnaOrganizzatore(altroOrganizzatore)
                .build();
        hack2.aggiungiTeam(teamAlpha);
        hackathonRepo.save(hack2);

        // 1. Segnalazione APERTA per org1 (Dovrebbe essere visibile)
        SegnalazioneViolazione s1 = new SegnalazioneViolazione(mentore, teamAlpha, hack1, "Violazione A");
        segnalazioneRepo.save(s1);

        // 2. Segnalazione GESTITA per org1 (NON dovrebbe essere visibile)
        SegnalazioneViolazione s2 = new SegnalazioneViolazione(mentore, teamAlpha, hack1, "Violazione B");
        s2.setProvvedimento(EsitoSegnalazione.RESPINTA, "Nessun problema");
        segnalazioneRepo.save(s2);

        // 3. Segnalazione APERTA per org2 (NON dovrebbe essere visibile a org1)
        SegnalazioneViolazione s3 = new SegnalazioneViolazione(mentore, teamAlpha, hack2, "Violazione C");
        segnalazioneRepo.save(s3);
    }

    @Test
    void getViolazioni_RestituisceSoloAperteDellOrganizzatoreLoggato() {
        sessione.setUtenteCorrente(organizzatore);

        List<SegnalazioneResponse> risultato = handler.getViolazioni();

        assertNotNull(risultato);
        assertEquals(1, risultato.size(), "Deve restituire solo 1 segnalazione");
        assertEquals("Violazione A", risultato.get(0).descrizione());
        assertEquals("APERTA", risultato.get(0).stato());
    }

    @Test
    void getViolazioni_FallisceSeNessunUtenteLoggato() {
        sessione.setUtenteCorrente(null);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> handler.getViolazioni());
        assertEquals("Nessun utente autenticato in sessione.", ex.getMessage());
    }
}