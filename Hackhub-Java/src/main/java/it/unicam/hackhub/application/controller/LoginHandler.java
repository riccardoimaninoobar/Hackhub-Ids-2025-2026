package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.UtenteRepository;

import java.util.Optional;

public class LoginHandler {
    private final UtenteRepository utenteRepo;

    public LoginHandler(UtenteRepository utenteRepo) {
        this.utenteRepo = utenteRepo;
    }

    public Utente elaboraLogin(String username, String password){
        validaDati(username, password);
        Optional<Utente> utenteContainer = utenteRepo.findById(username);
        if (utenteContainer.isEmpty()) {
            throw new IllegalArgumentException("Utente inesistente");
        }
        Utente utente = utenteContainer.get();
        if (!utente.verificaPassword(password)) {
            throw new IllegalArgumentException("Password errata");
        }
        return utente;
    }

    public void validaDati(String username, String password){
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Lo username non può essere vuoto.");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("La password deve contenere almeno 4 caratteri.");
        }
    }

}
