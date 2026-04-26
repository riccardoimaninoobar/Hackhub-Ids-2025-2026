package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginHandler {
    private final UtenteRepository utenteRepo;
    private final Sessione sessione; // AGGIUNTA

    public LoginHandler(UtenteRepository utenteRepo, Sessione sessione) {
        this.utenteRepo = utenteRepo;
        this.sessione = sessione;
    }

    public void elaboraLogin(String username, String password) {
        Optional<Utente> utenteContainer = utenteRepo.findByUsername(username);

        if (utenteContainer.isEmpty()) {
            throw new IllegalArgumentException("Utente inesistente");
        }
        Utente utente = utenteContainer.get();
        if (!utente.verificaPassword(password)) {
            throw new IllegalArgumentException("Password errata");
        }

        // --- LOGICA DI SESSIONE: Imposto l'utente come loggato ---
        sessione.setUtenteCorrente(utente);
    }
}