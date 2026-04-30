package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import it.unicam.hackhub.presentation.CliRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

// PASSAGGIO 1: Annotazioni Spring Boot
@SpringBootTest
@Transactional
class RegistrazioneHandlerTest {

    // PASSAGGIO 2: Disattivazione della CLI
    @MockitoBean
    private CliRunner cliRunner;

    // PASSAGGIO 3: Iniezione dei componenti reali
    @Autowired
    private RegistrazioneHandler handler;

    @Autowired
    private Sessione sessione;

    @Autowired
    private UtenteRepository utenteRepo;

    // PASSAGGIO 4: Setup iniziale
    @BeforeEach
    void setUp() {
        // Puliamo la sessione per garantire l'isolamento dei test
        sessione.setUtenteCorrente(null);
    }

    // PASSAGGIO 5: Test con database reale
    @Test
    void elaboraRegistrazione_SuccessoEAutoLogin() {
        // Act: Esegue la registrazione reale
        handler.elaboraRegistrazione("nuovoUtente", "email@test.it", "password123");

        // Assert: Verifica persistenza nel database H2
        assertTrue(utenteRepo.existsByUsername("nuovoUtente"),
                "L'utente dovrebbe essere stato salvato nel database.");

        // Assert: Verifica Auto-Login (Punto 5 del requisito)
        // Poiché handler e test usano lo stesso Bean 'Sessione', il cambiamento è visibile qui.
        assertNotNull(sessione.getUtenteCorrente(), "La sessione dovrebbe contenere l'utente dopo la registrazione.");
        assertEquals("nuovoUtente", sessione.getUtenteCorrente().getUsername());
    }

    @Test
    void elaboraRegistrazione_FallisceSeUtenteGiaEsistente() {
        // Arrange: Salviamo preventivamente un utente nel DB
        utenteRepo.save(new Utente("userEsistente", "vecchia@mail.it", "pass"));

        // Act & Assert: Verifichiamo che il database impedisca il duplicato tramite la logica dell'handler
        assertThrows(IllegalArgumentException.class, () ->
                handler.elaboraRegistrazione("userEsistente", "nuova@mail.it", "password")
        );
    }

    @Test
    void elaboraRegistrazione_ValidazioneEmailErrata() {
        // Act & Assert: Verifica la logica di validazione interna (se presente nel dominio o handler)
        assertThrows(IllegalArgumentException.class, () ->
                handler.elaboraRegistrazione("user", "email_senza_at", "12345")
        );
    }
}