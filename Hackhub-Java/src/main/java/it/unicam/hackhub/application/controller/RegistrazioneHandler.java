package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import org.springframework.stereotype.Service;

@Service
public class RegistrazioneHandler {

    private final UtenteRepository utenteRepository;
    private final Sessione sessione; // AGGIUNTA

    public RegistrazioneHandler(UtenteRepository utenteRepository, Sessione sessione) {
        this.utenteRepository = utenteRepository;
        this.sessione = sessione;
    }

    private void validaDati(String username, String email, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Lo username non può essere vuoto.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("L'e-mail inserita non è in un formato valido.");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("La password deve contenere almeno 4 caratteri.");
        }
    }

    private boolean verificaUtenteEsistente(String username) {
        return utenteRepository.existsByUsername(username);
    }

    public void elaboraRegistrazione(String username, String email, String password) {
        // Rimuovi il try-catch: la validazione deve bloccare l'esecuzione se i dati sono errati
        validaDati(username, email, password);

        if (verificaUtenteEsistente(username)) {
            throw new IllegalArgumentException("Esiste già un utente con lo stesso username o e-mail.");
        }

        Utente nuovoUtente = new Utente(username, email, password);
        utenteRepository.save(nuovoUtente);

        // AUTO-LOGIN
        sessione.setUtenteCorrente(nuovoUtente);
    }
}