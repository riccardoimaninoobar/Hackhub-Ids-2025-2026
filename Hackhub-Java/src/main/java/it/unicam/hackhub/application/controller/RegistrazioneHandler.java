package it.unicam.hackhub.application.controller;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.UtenteRepository;

public class RegistrazioneHandler {

    private final UtenteRepository utenteRepository;

    public RegistrazioneHandler(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    // --- STEP 3: Valida i dati inseriti ---
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

    // --- STEP 4: Verifica che non esista già un utente ---
    private boolean verificaUtenteEsistente(String username) {
        return utenteRepository.existsById(username);
    }

    // --- STEP 5: Crea il nuovo utente ---
    public Utente elaboraRegistrazione(String username, String email, String password) {
        validaDati(username, email, password);

        if (verificaUtenteEsistente(username)) {
            throw new IllegalArgumentException("Esiste già un utente con lo stesso username o e-mail.");
        }
        Utente nuovoUtente = new Utente(username, email, password);
        utenteRepository.save(nuovoUtente);
        return nuovoUtente;
    }
}