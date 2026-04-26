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

@SpringBootTest
@Transactional
class LogoutHandlerTest {

    // Blocchiamo l'interfaccia a riga di comando
    @MockBean
    private CliRunner cliRunner;

    // Lasciamo che Spring inietti i nostri componenti
    @Autowired
    private LogoutHandler handler;

    @Autowired
    private Sessione sessione;

    @Autowired
    private UtenteRepository utenteRepo;

    private Utente utenteLoggato;

    @BeforeEach
    void setUp() {
        // Pulizia preventiva della sessione prima di ogni singolo test
        sessione.setUtenteCorrente(null);

        // Prepariamo un utente fittizio per simulare il login e lo salviamo nel DB
        utenteLoggato = new Utente("utente.test", "test@email.it", "password123");
        utenteRepo.save(utenteLoggato);
    }

    // ==========================================================
    // TEST 1: Metodo verificaSessione()
    // ==========================================================

    @Test
    void verificaSessione_NonLanciaEccezioneSeUtenteLoggato() {
        // Arrange: impostiamo l'utente nella sessione iniettata da Spring
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