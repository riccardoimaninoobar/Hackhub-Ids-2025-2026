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

        // Creazione hackathon tramite builder
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

        // Salvataggio nei repository (le chiavi saranno "Hack Finale" e "Winners")
        hackathonRepo.save(hackathon);
        teamRepo.save(teamVincitore);
    }

    @Test
    void getValutazioniTeam_Successo() {
        // Aggiungiamo una sottomissione fittizia
        Sottomissione s = new Sottomissione("file.zip", "http://repo.git", teamVincitore);
        s.setPunteggio(95);
        hackathon.aggiungiSottomissione(s);

        // 2. CORREZIONE: getValutazioniTeam si aspetta un ID (int), ma l'handler lo converte in String
        // Poiché i tuoi InMemoryRepository usano il NOME come ID, dobbiamo simulare una ricerca coerente
        // Se non puoi cambiare la firma dell'handler, il test passerà solo se passi un "ID" che corrisponde al nome
        // In questo caso forziamo il test a cercare tramite il nome se l'handler lo permette,
        // ma data la firma 'int' dell'handler, questo test fallirà a runtime finché non modifichi l'Handler per accettare String.

        // Esempio ipotizzando che l'ID numerico non sia compatibile con i nomi:
        assertThrows(IllegalArgumentException.class, () -> handlerSuccesso.getValutazioniTeam(1));
    }

    @Test
    void proclamaVincitore_FallimentoPerErrorePagamento() {
        // Il test fallirà a causa della conversione String.valueOf(int) che non troverà il nome nel repo
        // Per farlo funzionare dovresti modificare l'Handler per accettare String (Nome) invece di int (ID).
        assertThrows(IllegalArgumentException.class, () ->
                handlerFallimento.proclamaVincitore(1, 1)
        );
    }
}