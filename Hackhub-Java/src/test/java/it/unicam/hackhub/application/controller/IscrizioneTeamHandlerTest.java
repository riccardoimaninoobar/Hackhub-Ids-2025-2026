package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.hackathon.Hackathon;
import it.unicam.hackhub.domain.model.hackathon.HackathonBuilder;
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
class IscrizioneTeamHandlerTest {

    // Blocca l'avvio della console
    @MockitoBean
    private CliRunner cliRunner;

    @Autowired
    private HackathonRepository hackathonRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private Sessione sessione;

    @Autowired
    private IscrizioneTeamHandler handler;

    private Utente utente;
    private Utente organizzatore;
    private Utente giudice;
    private Utente mentore;
    private Team team;

    @BeforeEach
    void setUp() {
        // 1. Creiamo e salviamo tutti gli utenti nel database
        utente = new Utente("membro1", "membro1@mail.it", "pass");
        organizzatore = new Utente("organizzatore", "org@mail.it", "pass");
        giudice = new Utente("giudice", "giudice@mail.it", "pass");
        mentore = new Utente("mentore", "mentore@mail.it", "pass");

        utenteRepository.save(utente);
        utenteRepository.save(organizzatore);
        utenteRepository.save(giudice);
        utenteRepository.save(mentore);

        // 2. Creiamo il team e lo salviamo
        team = new Team("TeamAlpha");
        teamRepository.save(team);

        // 3. Colleghiamo utente e team per mantenere la consistenza in RAM e nel DB
        team.aggiungiMembro(utente);
        utenteRepository.save(utente);

        // 4. Impostiamo l'utente loggato nella sessione
        sessione.setUtenteCorrente(utente);
    }

    @Test
    void getHackathonInIscrizione_restituisceGliHackathonDisponibili() {
        Hackathon h1 = creaHackathonInIscrizione("HackathonAI");
        Hackathon h2 = creaHackathonInIscrizione("HackathonWeb");

        // Salviamo nel DB gli hackathon appena creati
        hackathonRepository.save(h1);
        hackathonRepository.save(h2);

        Set<Hackathon> risultato = handler.getHackathonInIscrizione();

        assertNotNull(risultato);
        // Non usiamo assertEquals(2, ...) perché potrebbero esserci altri Hackathon nel DB.
        // Verifichiamo invece che i nostri siano stati pescati correttamente.
        assertTrue(risultato.stream().anyMatch(h -> h.getNome().equals("HackathonAI")));
        assertTrue(risultato.stream().anyMatch(h -> h.getNome().equals("HackathonWeb")));
    }

    @Test
    void iscriviTeam_teamDellUtenteVieneIscrittoConSuccesso() {
        Hackathon hackathon = creaHackathonInIscrizione("HackProva");
        hackathonRepository.save(hackathon);

        // L'utente loggato ("membro1") iscrive il suo team
        assertDoesNotThrow(() -> handler.iscriviTeam(hackathon));

        // Sincronizziamo il DB per sicurezza e verifichiamo l'esito recuperando i dati freschi
        hackathonRepository.flush();

        Hackathon salvato = hackathonRepository.findByNome("HackProva")
                .orElseThrow(() -> new AssertionError("Hackathon non trovato"));

        assertTrue(salvato.utentePartecipante(utente));
    }

    @Test
    void iscriviTeam_lanciaEccezione_seHackathonNonEPiuInIscrizione() {
        Hackathon hackathon = creaHackathonScaduto("HackScaduto");

        // Aggiorniamo forzatamente lo stato usando le date di scadenza (passerà a InCorso o Valutazione)
        hackathon.aggiornaStato();
        hackathonRepository.save(hackathon);

        assertThrows(RuntimeException.class, () -> handler.iscriviTeam(hackathon));
    }

    private Hackathon creaHackathonInIscrizione(String nome) {
        Hackathon h = new HackathonBuilder()
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
        h.aggiornaStato();
        return h;
    }

    private Hackathon creaHackathonScaduto(String nome) {
        Hackathon h = new HackathonBuilder()
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
        h.aggiornaStato();
        return h;
    }
}