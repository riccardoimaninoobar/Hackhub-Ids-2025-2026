package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.domain.model.*;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.TeamRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import it.unicam.hackhub.domain.service.SistemaPagamentoAdapter;
import it.unicam.hackhub.presentation.CliRunner;
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
    private CliRunner cliRunner;

    // Usiamo un mock per l'adapter di pagamento così possiamo decidere se deve fallire o meno
    @MockitoBean
    private SistemaPagamentoAdapter pagamentoAdapter;

    @Autowired
    private ProclamaVincitoreHandler handler;

    @Autowired
    private HackathonRepository hackathonRepo;

    @Autowired
    private TeamRepository teamRepo;

    @Autowired
    private UtenteRepository utenteRepo;

    private Hackathon hackathon;
    private Team teamVincitore;

    @BeforeEach
    void setUp() {
        // 1. Salvataggio Utenti
        Utente org = new Utente("org", "org@mail.com", "pass");
        Utente giudice = new Utente("giudice", "g@m.it", "p");
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
        // Aggiunta sottomissione (essendo legata all'hackathon salvato, viene gestita dal DB)
        Sottomissione s = new Sottomissione("progetto.zip", "http://github.com/win", teamVincitore);
        s.setPunteggio(95);
        hackathon.aggiungiSottomissione(s);
        hackathonRepo.save(hackathon); // Aggiorniamo l'hackathon con la sottomissione

        List<String> valutazioni = handler.getValutazioniTeam("Hack Finale");

        assertNotNull(valutazioni);
        assertEquals(1, valutazioni.size());
        assertTrue(valutazioni.get(0).contains("Winners"));
        assertTrue(valutazioni.get(0).contains("95"));
    }

    @Test
    void proclamaVincitore_Successo() {
        // Simuliamo che il pagamento vada a buon fine
        Mockito.when(pagamentoAdapter.erogaPagamento(Mockito.anyDouble(), Mockito.anyString()))
                .thenReturn(true);

        boolean esito = handler.proclamaVincitore("Hack Finale", "Winners");

        assertTrue(esito);

        // Ricarichiamo l'hackathon dal DB per verificare lo stato aggiornato
        Hackathon aggiornato = hackathonRepo.findByNome("Hack Finale").orElseThrow();
        assertNotNull(aggiornato.getTeamVincente());
        assertEquals("Winners", aggiornato.getTeamVincente().getNome());
        assertTrue(aggiornato.getStato() instanceof StatoConcluso);
    }

    @Test
    void proclamaVincitore_FallimentoPerErrorePagamento() {
        // Simuliamo un errore del sistema bancario
        Mockito.when(pagamentoAdapter.erogaPagamento(Mockito.anyDouble(), Mockito.anyString()))
                .thenReturn(false);

        boolean esito = handler.proclamaVincitore("Hack Finale", "Winners");

        assertFalse(esito);

        Hackathon aggiornato = hackathonRepo.findByNome("Hack Finale").orElseThrow();
        assertNull(aggiornato.getTeamVincente(), "Il team vincente non deve essere impostato se il pagamento fallisce");
    }

    @Test
    void proclamaVincitore_FallimentoPerHackathonInesistente() {
        assertThrows(IllegalArgumentException.class, () ->
                handler.proclamaVincitore("Hackathon Finto", "Winners")
        );
    }
}