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
import it.unicam.hackhub.presentation.CliRunner;
import it.unicam.hackhub.presentation.dto.HackathonSupportoResponse;
import it.unicam.hackhub.presentation.dto.SegnalazioneRequest;
import it.unicam.hackhub.presentation.dto.TeamResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SegnalaViolazioneHandlerTest {

    @MockitoBean
    private CliRunner cliRunner;

    @Autowired
    private SegnalaViolazioneHandler handler;

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

    private Utente mentore;
    private Utente organizzatore;
    private Team teamSegnalato;
    private Hackathon hackathon;

    @BeforeEach
    void setUp() {
        // 1. Pulizia sessione
        sessione.setUtenteCorrente(null);

        // 2. Creazione e salvataggio Utenti
        mentore = new Utente("mentoreFiscale", "mentore@mail.it", "pass");
        organizzatore = new Utente("orgMaster", "org@mail.it", "pass");
        utenteRepo.save(mentore);
        utenteRepo.save(organizzatore);

        // 3. Creazione e salvataggio Team
        teamSegnalato = new Team("Team Furbetti");
        teamRepo.save(teamSegnalato);

        // 4. Creazione Hackathon con l'organizzatore e assegnazione del mentore
        hackathon = new HackathonBuilder()
                .assegnaNome("HackSecurity")
                .assegnaOrganizzatore(organizzatore)
                .assegnaDataInizio(LocalDate.now().minusDays(1))
                .assegnaDataFine(LocalDate.now().plusDays(5))
                .assegnaMentore(mentore)
                .build();

        // Iscrizione del team all'hackathon (sfrutta la logica di Partecipazione dietro le quinte)
        hackathon.aggiungiTeam(teamSegnalato);
        hackathonRepo.save(hackathon);
    }

    // ==========================================================
    // TEST: getHackathonsAssegnati
    // ==========================================================
    @Test
    void getHackathonsAssegnati_RestituisceListaCorrettaPerIlMentore() {
        sessione.setUtenteCorrente(mentore);

        List<HackathonSupportoResponse> risultato = handler.getHackathonsAssegnati();

        assertNotNull(risultato);
        assertEquals(1, risultato.size());
        assertEquals("HackSecurity", risultato.get(0).nome());
    }

    @Test
    void getHackathonsAssegnati_LanciaEccezioneSeNonLoggato() {
        sessione.setUtenteCorrente(null);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> handler.getHackathonsAssegnati());
        assertEquals("Accesso negato: nessun utente in sessione.", ex.getMessage());
    }

    // ==========================================================
    // TEST: getTeamPartecipanti
    // ==========================================================
    @Test
    void getTeamPartecipanti_RestituisceTeamCorretti() {
        sessione.setUtenteCorrente(mentore);

        List<TeamResponse> risultato = handler.getTeamPartecipanti(hackathon.getId());

        assertNotNull(risultato);
        assertEquals(1, risultato.size());
        assertEquals("Team Furbetti", risultato.get(0).nome());
    }

    @Test
    void getTeamPartecipanti_LanciaEccezioneSeHackathonInesistente() {
        sessione.setUtenteCorrente(mentore);

        assertThrows(IllegalArgumentException.class, () -> handler.getTeamPartecipanti(999L));
    }

    // ==========================================================
    // TEST: inserisciSegnalazione
    // ==========================================================
    @Test
    void inserisciSegnalazione_SalvaCorrettamenteNelDbEInizializzaStato() {
        sessione.setUtenteCorrente(mentore);

        SegnalazioneRequest request = new SegnalazioneRequest(
                hackathon.getId(),
                teamSegnalato.getId(),
                "Utilizzo di API non consentite dal regolamento."
        );

        assertDoesNotThrow(() -> handler.inserisciSegnalazione(request));

        // Verifichiamo che la segnalazione sia effettivamente nel Database
        assertEquals(1, segnalazioneRepo.count(), "Dovrebbe esserci esattamente una segnalazione nel DB");

        SegnalazioneViolazione salvata = segnalazioneRepo.findAll().get(0);
        assertEquals("Utilizzo di API non consentite dal regolamento.", salvata.getDescrizione());
        assertEquals(EsitoSegnalazione.APERTA, salvata.getStato(), "La segnalazione deve nascere con stato APERTA");
        assertEquals(mentore.getId(), salvata.getMentore().getId());
        assertEquals(teamSegnalato.getId(), salvata.getTeamSegnalato().getId());
        assertEquals(hackathon.getId(), salvata.getHackathon().getId());
    }

    @Test
    void inserisciSegnalazione_FallisceSeTeamNonIscrittoAllHackathon() {
        sessione.setUtenteCorrente(mentore);

        // Creiamo un team intruso non iscritto all'hackathon
        Team teamIntruso = new Team("Team Intruso");
        teamRepo.save(teamIntruso);

        SegnalazioneRequest request = new SegnalazioneRequest(
                hackathon.getId(),
                teamIntruso.getId(),
                "Non dovrebbero essere qui"
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> handler.inserisciSegnalazione(request));
        assertTrue(ex.getMessage().contains("Il team selezionato non partecipa"));
    }

    @Test
    void inserisciSegnalazione_LanciaEccezioneSeNonLoggato() {
        sessione.setUtenteCorrente(null);

        SegnalazioneRequest request = new SegnalazioneRequest(hackathon.getId(), teamSegnalato.getId(), "Test");

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> handler.inserisciSegnalazione(request));
        assertEquals("Operazione non autorizzata.", ex.getMessage());
    }
}