package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.domain.model.*;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.TeamRepository;
import it.unicam.hackhub.domain.service.SistemaPagamentoAdapter;
import it.unicam.hackhub.infrastructure.persistence.InMemoryHackathonRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryTeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProclamaVincitoreHandlerTest {

    private ProclamaVincitoreHandler handlerSuccesso;
    private ProclamaVincitoreHandler handlerFallimento;
    private HackathonRepository hackathonRepo;
    private TeamRepository teamRepo;

    private Hackathon hackathon;
    private Team teamVincitore;

    @BeforeEach
    void setUp() {
        hackathonRepo = new InMemoryHackathonRepository();
        teamRepo = new InMemoryTeamRepository();

        SistemaPagamentoAdapter pagamentoSuccess = new SistemaPagamentoAdapter() {
            @Override
            public boolean erogaPremio(double importo, String datiBancari) {
                return true;
            }
        };

        SistemaPagamentoAdapter pagamentoFail = new SistemaPagamentoAdapter() {
            @Override
            public boolean erogaPremio(double importo, String datiBancari) {
                return false;
            }
        };

        handlerSuccesso = new ProclamaVincitoreHandler(hackathonRepo, teamRepo, pagamentoSuccess);
        handlerFallimento = new ProclamaVincitoreHandler(hackathonRepo, teamRepo, pagamentoFail);

        Utente org = new Utente("org", "org@mail.com", "pass");

        // Creazione hackathon (Nome: "Hack Finale")
        hackathon = new HackathonBuilder()
                .assegnaNome("Hack Finale")
                .assegnaOrganizzatore(org)
                .assegnaPremioImporto(new BigDecimal("1000"))
                .assegnaGiudice(new Utente("giudice", "g@m.it", "p"))
                .build();

        // Impostiamo lo stato necessario per la valutazione
        hackathon.setStato(new StatoInValutazione());

        teamVincitore = new Team("Winners");
        teamVincitore.setDatiBancari("IBAN123456789");

        // Iscrizione del team all'hackathon
        hackathon.aggiungiTeam(teamVincitore);

        // Salvataggio nei repository
        hackathonRepo.save(hackathon);
        teamRepo.save(teamVincitore);
    }

    @Test
    void getValutazioniTeam_Successo() {
        // Aggiunta sottomissione
        Sottomissione s = new Sottomissione("progetto.zip", "http://github.com/win", teamVincitore);
        s.setPunteggio(95);
        hackathon.aggiungiSottomissione(s);

        // Ora passiamo la stringa con il nome esatto salvato nel repository
        List<String> valutazioni = handlerSuccesso.getValutazioniTeam("Hack Finale");

        assertNotNull(valutazioni);
        assertEquals(1, valutazioni.size());
        assertTrue(valutazioni.get(0).contains("Winners"));
        assertTrue(valutazioni.get(0).contains("95"));
    }

    @Test
    void proclamaVincitore_Successo() {
        // Test base: il team esiste, l'hackathon esiste ed è in valutazione, il pagamento va a buon fine
        boolean esito = handlerSuccesso.proclamaVincitore("Hack Finale", "Winners");

        assertTrue(esito);
        assertNotNull(hackathon.getTeamVincente());
        assertEquals("Winners", hackathon.getTeamVincente().getName());

        // Verifica che lo stato sia effettivamente cambiato in Concluso
        assertTrue(hackathon.getStato() instanceof StatoConcluso || "Concluso".equalsIgnoreCase(hackathon.getStato().toString()));
    }

    @Test
    void proclamaVincitore_FallimentoPerErrorePagamento() {
        // Se il pagamento fallisce, il metodo ritorna false (non lancia eccezioni)
        boolean esito = handlerFallimento.proclamaVincitore("Hack Finale", "Winners");

        assertFalse(esito);
        assertNull(hackathon.getTeamVincente());
    }

    @Test
    void proclamaVincitore_FallimentoPerHackathonInesistente() {
        // Testiamo che cerchi correttamente l'Hackathon usando la Stringa e lanci l'eccezione se non lo trova
        assertThrows(IllegalArgumentException.class, () ->
                handlerSuccesso.proclamaVincitore("Hackathon Finto", "Winners")
        );
    }
}