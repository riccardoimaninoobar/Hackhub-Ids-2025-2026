package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Utente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogoutHandlerTest {

    private LogoutHandler handler;
    private Sessione sessione;
    private Utente utenteLoggato;

    @BeforeEach
    void setUp() {
        // Inizializziamo la sessione (vuota di default)
        sessione = new Sessione(null);
        handler = new LogoutHandler(sessione);

        // Prepariamo un utente fittizio per simulare il login
        utenteLoggato = new Utente("utente.test", "test@email.it", "password123");
    }

    // ==========================================================
    // TEST 1: Metodo verificaSessione()
    // ==========================================================

    @Test
    void verificaSessione_NonLanciaEccezioneSeUtenteLoggato() {
        // Arrange: impostiamo un utente nella sessione
        sessione.setUtenteCorrente(utenteLoggato);

        // Act & Assert: verifichiamo che il controllo passi senza eccezioni
        assertDoesNotThrow(() -> {
            handler.verificaSessione();
        }, "Non dovrebbe lanciare eccezioni se c'è un utente loggato nella sessione.");
    }

    @Test
    void verificaSessione_LanciaEccezioneSeNessunUtenteLoggato() {
        // Arrange: ci assicuriamo che la sessione sia vuota
        sessione.setUtenteCorrente(null);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            handler.verificaSessione();
        });

        assertEquals("Nessun utente attualmente loggato. Impossibile effettuare il logout.", exception.getMessage());
    }

    // ==========================================================
    // TEST 2: Metodo effettuaLogout()
    // ==========================================================

    @Test
    void effettuaLogout_AzzeraCorrettamenteLaSessione() {
        // Arrange: partiamo da una situazione in cui l'utente è loggato
        sessione.setUtenteCorrente(utenteLoggato);
        assertNotNull(sessione.getUtenteCorrente(), "L'utente dovrebbe essere loggato prima del test.");

        // Act: eseguiamo il logout
        handler.effettuaLogout();

        // Assert: verifichiamo che l'utente corrente sia tornato a null
        assertNull(sessione.getUtenteCorrente(), "L'utente corrente nella sessione deve essere null dopo il logout.");
    }
}