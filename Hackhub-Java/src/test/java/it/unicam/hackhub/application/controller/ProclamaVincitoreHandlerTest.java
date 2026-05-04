package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione; // <-- IMPORTANTE
import it.unicam.hackhub.domain.model.*;
import it.unicam.hackhub.domain.model.hackathon.Hackathon;
import it.unicam.hackhub.domain.model.hackathon.HackathonBuilder;
import it.unicam.hackhub.domain.model.hackathon.state.StatoConcluso;
import it.unicam.hackhub.domain.model.hackathon.state.StatoInValutazione;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.TeamRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import it.unicam.hackhub.domain.service.SistemaPagamentoAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProclamaVincitoreHandlerTest {

    @MockitoBean
    private SistemaPagamentoAdapter pagamentoAdapter;

    @Autowired
    private ProclamaVincitoreHandler handler;

    @Autowired
    private Sessione sessione; // <-- Aggiunta la sessione per i test di sicurezza

    @Autowired
    private HackathonRepository hackathonRepo;

    @Autowired
    private TeamRepository teamRepo;

    @Autowired
    private UtenteRepository utenteRepo;

    private Hackathon hackathon;
    private Team teamVincitore;

    // Promossi a variabili di istanza per usarli nei test
    private Utente org;
    private Utente giudice;

    @BeforeEach
    void setUp() {
        sessione.setUtenteCorrente(null); // Assicura un ambiente pulito per ogni test

        // 1. Salvataggio Utenti
        org = new Utente("org", "org@mail.com", "pass");
        giudice = new Utente("giudice", "g@m.it", "p");
        utenteRepo.save(org);
        utenteRepo.save(giudice);

        // 2. Salvataggio Team
        teamVincitore = new Team("Winners");
        teamVincitore.setDatiBancari("IBAN123456789");
        teamRepo.save(teamVincitore);

        // 3. Salvataggio Hackathon
        hackathon = new HackathonBuilder()
                .assegnaNome("Hack Finale")
                .assegnaOrganizzatore(org)
                .assegnaPremioImporto(new BigDecimal("1000"))
                .assegnaGiudice(giudice)
                .build();

        hackathon.setStato(new StatoInValutazione());
        hackathon.aggiungiTeam(teamVincitore);

        hackathonRepo.save(hackathon);
    }

    @Test
    void getValutazioniTeam_Successo() {
        // Aggiunta sottomissione
        Sottomissione s = new Sottomissione("progetto.zip", "http://github.com/win", teamVincitore);
        s.setPunteggio(95);
        hackathon.aggiungiSottomissione(s);
        hackathonRepo.save(hackathon);

        List<String> valutazioni = handler.getValutazioniTeam("Hack Finale");

        assertNotNull(valutazioni);
        assertEquals(1, valutazioni.size());
        assertTrue(valutazioni.get(0).contains("Winners"));
        assertTrue(valutazioni.get(0).contains("95"));
    }

    @Test
    void proclamaVincitore_Successo() {
        sessione.setUtenteCorrente(org); // <-- Simula il login dell'organizzatore autorizzato

        Mockito.when(pagamentoAdapter.erogaPagamento(Mockito.anyDouble(), Mockito.anyString()))
                .thenReturn(true);

        boolean esito = handler.proclamaVincitore("Hack Finale", "Winners");

        assertTrue(esito);

        Hackathon aggiornato = hackathonRepo.findByNome("Hack Finale").orElseThrow();
        assertNotNull(aggiornato.getTeamVincente());
        assertEquals("Winners", aggiornato.getTeamVincente().getNome());
        assertTrue(aggiornato.getStato() instanceof StatoConcluso);
    }

    @Test
    void proclamaVincitore_FallimentoPerErrorePagamento() {
        sessione.setUtenteCorrente(org); // <-- Simula il login

        Mockito.when(pagamentoAdapter.erogaPagamento(Mockito.anyDouble(), Mockito.anyString()))
                .thenReturn(false);

        boolean esito = handler.proclamaVincitore("Hack Finale", "Winners");

        assertFalse(esito);

        Hackathon aggiornato = hackathonRepo.findByNome("Hack Finale").orElseThrow();
        assertNull(aggiornato.getTeamVincente(), "Il team vincente non deve essere impostato se il pagamento fallisce");
    }

    @Test
    void proclamaVincitore_FallimentoPerHackathonInesistente() {
        sessione.setUtenteCorrente(org); // <-- Simula il login

        assertThrows(IllegalArgumentException.class, () ->
                handler.proclamaVincitore("Hackathon Finto", "Winners")
        );
    }

    // ==========================================================
    // NUOVI TEST DI SICUREZZA
    // ==========================================================

    @Test
    void proclamaVincitore_FallisceSeNonLoggato() {
        sessione.setUtenteCorrente(null); // Nessuno in sessione

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                handler.proclamaVincitore("Hack Finale", "Winners")
        );
        assertEquals("Operazione non autorizzata.", ex.getMessage());
    }

    @Test
    void proclamaVincitore_FallisceSeNonOrganizzatore() {
        sessione.setUtenteCorrente(giudice); // <-- Simula il login di un utente NON autorizzato (es. il giudice)

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                handler.proclamaVincitore("Hack Finale", "Winners")
        );
        assertEquals("Solo l'organizzatore dell'Hackathon può proclamare il vincitore.", ex.getMessage());
    }
}