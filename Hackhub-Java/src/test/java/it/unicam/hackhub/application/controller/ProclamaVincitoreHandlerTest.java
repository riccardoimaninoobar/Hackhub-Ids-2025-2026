package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.domain.model.*;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.TeamRepository;
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

        // Adapter Mock che simula erogazione di premio avvenuta con successo
        SistemaPagamentoAdapter pagamentoSuccess = new SistemaPagamentoAdapter() {
            @Override
            public boolean erogaPremio(BigDecimal importo, String datiBancari) {
                return true;
            }
        };

        // Adapter Mock che simula un fallimento nell'erogazione del premio
        SistemaPagamentoAdapter pagamentoFail = new SistemaPagamentoAdapter() {
            @Override
            public boolean erogaPremio(BigDecimal importo, String datiBancari) {
                return false;
            }
        };

        handlerSuccesso = new ProclamaVincitoreHandler(hackathonRepo, teamRepo, pagamentoSuccess);
        handlerFallimento = new ProclamaVincitoreHandler(hackathonRepo, teamRepo, pagamentoFail);

        Utente org = new Utente("org", "org@mail.com", "pass");
        hackathon = new HackathonBuilder()
                .assegnaNome("Hack Finale")
                .assegnaOrganizzatore(org)
                .assegnaPremioImporto(new BigDecimal("1000"))
                .build();
        hackathon.setStato(new StatoInValutazione());

        teamVincitore = new Team("Winners");
        teamVincitore.setDatiBancari("IBAN123456789");

        hackathonRepo.save(hackathon);
        teamRepo.save(teamVincitore);
    }

    @Test
    void getValutazioniTeam_Successo() {
        int hackId = Integer.parseInt(hackathon.getId() != null ? hackathon.getId() : "1");

        // Aggiungiamo una sottomissione fittizia per validare il recupero delle valutazioni
        Sottomissione s = new Sottomissione("http://repo.git", teamVincitore);
        s.setPunteggio(95);
        hackathon.getSottomissioni().add(s);

        List<String> valutazioni = handlerSuccesso.getValutazioniTeam(hackId);

        assertFalse(valutazioni.isEmpty(), "La lista delle valutazioni non dovrebbe essere vuota");
        assertTrue(valutazioni.get(0).contains("95"));
        assertTrue(valutazioni.get(0).contains(teamVincitore.getName()));
    }

    @Test
    void proclamaVincitore_ErogaPremio_Successo() {
        // Arrange
        int hackId = Integer.parseInt(hackathon.getId() != null ? hackathon.getId() : "1");
        int teamId = Integer.parseInt(teamVincitore.getId() != null ? teamVincitore.getId() : "1");

        // Act
        boolean risultato = handlerSuccesso.proclamaVincitore(hackId, teamId);

        // Assert
        assertTrue(risultato, "Il pagamento e la proclamazione dovrebbero concludersi con successo");
        assertEquals(teamVincitore, hackathon.getTeamVincente());
        assertTrue(hackathon.getStato() instanceof StatoConcluso || "Concluso".equalsIgnoreCase(hackathon.getStato()));
    }

    @Test
    void proclamaVincitore_FallimentoPerErrorePagamento() {
        // Arrange
        int hackId = Integer.parseInt(hackathon.getId() != null ? hackathon.getId() : "1");
        int teamId = Integer.parseInt(teamVincitore.getId() != null ? teamVincitore.getId() : "1");

        // Act
        boolean risultato = handlerFallimento.proclamaVincitore(hackId, teamId);

        // Assert
        assertFalse(risultato, "Il pagamento fallito dovrebbe impedire la proclamazione del vincitore");
        assertNull(hackathon.getTeamVincente(), "Nessun team vincitore dovrebbe essere stato assegnato");
    }

    @Test
    void proclamaVincitore_LanciaEccezioneSeNonInValutazione() {
        hackathon.setStato(new StatoInCorso());
        int hackId = Integer.parseInt(hackathon.getId() != null ? hackathon.getId() : "1");
        int teamId = Integer.parseInt(teamVincitore.getId() != null ? teamVincitore.getId() : "1");

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> handlerSuccesso.proclamaVincitore(hackId, teamId));
        assertTrue(ex.getMessage().contains("non è in stato 'In valutazione'"));
    }
}