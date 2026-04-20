package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryUtenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrazioneHandlerTest {

    private RegistrazioneHandler handler;
    private Sessione sessione;
    private UtenteRepository utenteRepo;

    @BeforeEach
    void setUp() {
        sessione = new Sessione(null);
        utenteRepo = new InMemoryUtenteRepository();
        handler = new RegistrazioneHandler(utenteRepo, sessione);
    }

    @Test
    void elaboraRegistrazione_SuccessoEAutoLogin() {
        // Esegue la registrazione
        handler.elaboraRegistrazione("nuovoUtente", "email@test.it", "password123");

        // Verifica persistenza
        assertTrue(utenteRepo.existsById("nuovoUtente"));

        // Verifica Auto-Login (Punto 5 del requisito)
        assertNotNull(sessione.getUtenteCorrente());
        assertEquals("nuovoUtente", sessione.getUtenteCorrente().getUsername());
    }

    @Test
    void elaboraRegistrazione_FallisceSeUtenteGiaEsistente() {
        utenteRepo.save(new Utente("userEsistente", "vecchia@mail.it", "pass"));

        assertThrows(IllegalArgumentException.class, () ->
                handler.elaboraRegistrazione("userEsistente", "nuova@mail.it", "password")
        );
    }

    @Test
    void elaboraRegistrazione_ValidazioneEmailErrata() {
        // L'handler cattura l'eccezione internamente o la lancia a seconda dell'implementazione.
        // Se l'implementazione attuale lancia l'eccezione:
        assertThrows(IllegalArgumentException.class, () ->
                handler.elaboraRegistrazione("user", "email_senza_at", "12345")
        );
    }
}