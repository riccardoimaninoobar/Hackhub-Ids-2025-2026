package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.invito.Invito;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.InvitoRepository;
import it.unicam.hackhub.domain.repository.TeamRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // Svuota il DB dopo ogni test
class AccettazioneInvitoHandlerTest {


    @Autowired private AccettazioneInvitoHandler handler;
    @Autowired private Sessione sessione;
    @Autowired private InvitoRepository invitoRepo;
    @Autowired private UtenteRepository utenteRepo;
    @Autowired private TeamRepository teamRepo;

    private Utente utenteInvitato;
    private Team teamMittente;
    private Invito invito;

    @BeforeEach
    void setUp() {
        // Creiamo e SALVIAMO nel VERO database
        utenteInvitato = new Utente("invitedUser", "invited@mail.com", "pass");
        utenteRepo.save(utenteInvitato);

        teamMittente = new Team("Omega Team");
        teamRepo.save(teamMittente);

        invito = new Invito(utenteInvitato, teamMittente);
        invitoRepo.save(invito);
    }

    @Test
    void getInvitiPendenti_Successo() {
        sessione.setUtenteCorrente(utenteInvitato);

        List<Invito> pendenti = handler.getInvitiPendenti();

        assertFalse(pendenti.isEmpty(), "La lista degli inviti non dovrebbe essere vuota");
        assertEquals("IN_ATTESA", pendenti.get(0).getNomeStato());
    }

    @Test
    void accettaInvito_Successo() {
        sessione.setUtenteCorrente(utenteInvitato);

        assertDoesNotThrow(() -> handler.accettaInvito(invito));

        // Ricarichiamo dal database per verificare che JPA abbia fatto il suo dovere!
        Invito invitoAggiornato = invitoRepo.findById(invito.getId()).get();
        assertEquals("ACCETTATO", invitoAggiornato.getNomeStato(), "Lo stato dell'invito salvato nel DB deve essere ACCETTATO");

        Utente utenteAggiornato = utenteRepo.findById(utenteInvitato.getId()).get();
        assertNotNull(utenteAggiornato.getTeam(), "L'utente dovrebbe far parte di un team");
        assertEquals(teamMittente.getId(), utenteAggiornato.getTeam().getId(), "Il team dell'utente deve combaciare con quello del mittente");
    }
}