package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Invito;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.InvitoRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryInvitoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccettazioneInvitoHandlerTest {

    private AccettazioneInvitoHandler handler;
    private Sessione sessione;
    private InvitoRepository invitoRepo;

    private Utente utenteInvitato;
    private Team teamMittente;
    private Invito invito;

    @BeforeEach
    void setUp() {
        sessione = new Sessione(null);
        invitoRepo = new InMemoryInvitoRepository();
        handler = new AccettazioneInvitoHandler(invitoRepo, sessione);

        utenteInvitato = new Utente("invitedUser", "invited@mail.com", "pass");
        teamMittente = new Team("Omega Team");

        invito = new Invito(utenteInvitato, teamMittente);
        invitoRepo.save(invito);
    }

    @Test
    void getInvitiPendenti_Successo() {
        sessione.setUtenteCorrente(utenteInvitato);

        List<Invito> pendenti = handler.getInvitiPendenti();

        assertNotNull(pendenti, "La lista degli inviti non dovrebbe essere nulla");
    }

    @Test
    void accettaInvito_Successo() {
        sessione.setUtenteCorrente(utenteInvitato);

        assertDoesNotThrow(() -> handler.accettaInvito(invito));

        // Verifichiamo il corretto mutamento di stato dell'invito
        assertNotEquals("IN_ATTESA", invito.getNomeStato(), "Lo stato dell'invito non dovrebbe più essere IN_ATTESA");
        // Verifichiamo che l'utente adesso faccia parte del Team mittente
        assertEquals(teamMittente, utenteInvitato.getTeam(), "L'utente dovrebbe essere stato aggiunto al team");
    }
}