package it.unicam.hackhub.application.controller;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.UtenteRepository;

public class RegistrazioneHandler {

    private final UtenteRepository utenteRepository;

    public RegistrazioneHandler(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    public void validaEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("L'e-mail inserita non è in un formato valido.");
        }
    }

    // --- STEP 3: Valida i dati inseriti ---
    public void validaDati(String username, String email, String password) throws IllegalArgumentException {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Il username non può essere vuoto.");
        }
        validaEmail(email);
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("La password deve contenere almeno 4 caratteri.");
        }
    }

    // --- STEP 4: Verifica che non esista già un utente ---
    public boolean verificaUtenteEsistente(String username, String email) {
        return utenteRepository.findAll().stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username) || u.getEmail().equalsIgnoreCase(email));
    }

    // --- STEP 5: Crea il nuovo utente ---
    public Utente registraUtente(String username, String email, String password) {
        Utente nuovoUtente = new Utente(username, email, password);
        utenteRepository.save(nuovoUtente);
        return nuovoUtente;
    }
}