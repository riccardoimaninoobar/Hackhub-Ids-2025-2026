package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import it.unicam.hackhub.presentation.CliRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

// PASSAGGIO 1: Annotazioni Spring
@SpringBootTest
@Transactional
class LoginHandlerTest {

    // PASSAGGIO 2: Blocchiamo la console
    @MockBean
    private CliRunner cliRunner;

    // PASSAGGIO 3: Niente più 'new' o Mockito, lasciamo fare a Spring
    @Autowired
    private LoginHandler handler;

    @Autowired
    private Sessione sessione;

    @Autowired
    private UtenteRepository utenteRepo;

    private Utente utenteValido;

    // PASSAGGIO 4: Salvataggio nel vero database H2
    @BeforeEach
    void setUp() {
        // Pulizia preventiva: assicuriamoci che la sessione sia vuota all'avvio del test
        sessione.setUtenteCorrente(null);

        // Prepariamo un utente reale e lo salviamo nel DB
        utenteValido = new Utente("mario.rossi", "mario@email.it", "passwordSicura123");
        utenteRepo.save(utenteValido);
    }

    // PASSAGGIO 5: Esecuzione del test (la logica rimane invariata)
    // ==========================================================
    // TEST: Metodo elaboraLogin(username, password)
    // ==========================================================

    @Test
    void elaboraLogin_Successo() {
        // Act
        assertDoesNotThrow(() -> {
            handler.elaboraLogin("mario.rossi", "passwordSicura123");
        });

        // Assert: verifichiamo che l'utente sia stato caricato correttamente nella sessione
        assertNotNull(sessione.getUtenteCorrente(), "La sessione non dovrebbe essere nulla dopo un login con successo.");

        // Confrontiamo gli ID o gli Username per essere sicuri che sia lo stesso utente del DB
        assertEquals(utenteValido.getUsername(), sessione.getUtenteCorrente().getUsername(), "L'utente loggato deve corrispondere a quello salvato nel repository.");
    }

    @Test
    void elaboraLogin_LanciaEccezioneSeUtenteInesistente() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            handler.elaboraLogin("utenteGhost", "passwordQualsiasi");
        });

        assertEquals("Utente inesistente", exception.getMessage());

        // Verifichiamo che la sessione sia rimasta vuota
        assertNull(sessione.getUtenteCorrente(), "Nessun utente deve essere loggato se l'username non esiste.");
    }

    @Test
    void elaboraLogin_LanciaEccezioneSePasswordErrata() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            handler.elaboraLogin("mario.rossi", "passwordSbagliata");
        });

        assertEquals("Password errata", exception.getMessage());

        // Verifichiamo che la sessione sia rimasta vuota
        assertNull(sessione.getUtenteCorrente(), "Nessun utente deve essere loggato se la password è errata.");
    }
}