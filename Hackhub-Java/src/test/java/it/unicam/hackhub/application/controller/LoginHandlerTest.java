package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryUtenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginHandlerTest {

    private LoginHandler handler;
    private Sessione sessione;
    private UtenteRepository utenteRepo;

    private Utente utenteValido;

    @BeforeEach
    void setUp() {
        // Inizializziamo le dipendenze
        sessione = new Sessione(null);
        utenteRepo = new InMemoryUtenteRepository();
        handler = new LoginHandler(utenteRepo, sessione);

        // Prepariamo un utente fittizio per i test e lo salviamo nel repository
        utenteValido = new Utente("mario.rossi", "mario@email.it", "passwordSicura123");
        utenteRepo.save(utenteValido);
    }

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
        assertEquals(utenteValido, sessione.getUtenteCorrente(), "L'utente loggato deve corrispondere a quello salvato nel repository.");
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