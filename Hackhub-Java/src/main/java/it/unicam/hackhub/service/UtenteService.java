package it.unicam.hackhub.service;

import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.exception.AuthenticationException;
import it.unicam.hackhub.exception.HackHubException;
import it.unicam.hackhub.exception.ValidationException;
import it.unicam.hackhub.repository.UtenteRepository;
import it.unicam.hackhub.service.dto.LoginRequest;
import it.unicam.hackhub.service.dto.RegistrazioneRequest;
import it.unicam.hackhub.service.dto.UtenteResponse;
import it.unicam.hackhub.validation.InputValidator;
import it.unicam.hackhub.validation.PasswordValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service per la gestione degli utenti (registrazione, login, ecc.).
 */
public class UtenteService {

    private final UtenteRepository utenteRepository;

    public UtenteService(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    /**
     * Registra un nuovo utente.
     * @param request i dati di registrazione
     * @return l'utente registrato
     * @throws ValidationException se i dati non sono validi
     * @throws HackHubException se username o email sono già registrati
     */
    public UtenteResponse registra(RegistrazioneRequest request) {
        // Valida tutti i campi
        List<String> errori = validaRegistrazione(request);
        if (!errori.isEmpty()) {
            throw new ValidationException(String.join("; ", errori));
        }

        // Verifica unicità username
        if (utenteRepository.existsByUsername(request.username())) {
            throw new HackHubException("Username già registrato: " + request.username());
        }

        // Verifica unicità email
        if (utenteRepository.existsByEmail(request.email())) {
            throw new HackHubException("Email già registrata: " + request.email());
        }

        // Crea e salva l'utente
        Utente utente = new Utente(
            request.username(),
            request.email(),
            request.password(), // In produzione dovrebbe essere hashata
            request.nome(),
            request.cognome()
        );

        utente = utenteRepository.save(utente);
        return UtenteResponse.fromEntity(utente);
    }

    /**
     * Effettua il login di un utente.
     * @param request le credenziali di login
     * @return l'utente autenticato
     * @throws AuthenticationException se le credenziali non sono valide
     */
    public UtenteResponse login(LoginRequest request) {
        if (request.username().isBlank()) {
            throw new AuthenticationException("Username obbligatorio");
        }
        if (request.password().isBlank()) {
            throw new AuthenticationException("Password obbligatoria");
        }

        // Cerca l'utente per username
        Optional<Utente> utenteOpt = utenteRepository.findByUsername(request.username());

        if (utenteOpt.isEmpty()) {
            throw new AuthenticationException("Credenziali non valide");
        }

        Utente utente = utenteOpt.get();

        // Verifica la password (confronto diretto, in produzione usare hash)
        if (!utente.getPassword().equals(request.password())) {
            throw new AuthenticationException("Credenziali non valide");
        }

        return UtenteResponse.fromEntity(utente);
    }

    /**
     * Trova un utente per ID.
     */
    public Optional<UtenteResponse> findById(Long id) {
        return utenteRepository.findById(id)
                .map(UtenteResponse::fromEntity);
    }

    /**
     * Trova un utente per username.
     */
    public Optional<UtenteResponse> findByUsername(String username) {
        return utenteRepository.findByUsername(username)
                .map(UtenteResponse::fromEntity);
    }

    /**
     * Restituisce tutti gli utenti registrati.
     */
    public List<UtenteResponse> findAll() {
        return utenteRepository.findAll().stream()
                .map(UtenteResponse::fromEntity)
                .toList();
    }

    /**
     * Conta il numero di utenti registrati.
     */
    public long count() {
        return utenteRepository.count();
    }

    /**
     * Restituisce l'entità Utente per uso interno (es. creazione team).
     */
    public Optional<Utente> getUtenteEntity(Long id) {
        return utenteRepository.findById(id);
    }

    /**
     * Restituisce l'entità Utente per username.
     */
    public Optional<Utente> getUtenteEntityByUsername(String username) {
        return utenteRepository.findByUsername(username);
    }

    /**
     * Valida i dati di registrazione.
     * @return lista di errori (vuota se tutto ok)
     */
    private List<String> validaRegistrazione(RegistrazioneRequest request) {
        List<String> errori = new ArrayList<>();

        String usernameError = InputValidator.validateUsername(request.username());
        if (usernameError != null) {
            errori.add(usernameError);
        }

        String emailError = InputValidator.validateEmail(request.email());
        if (emailError != null) {
            errori.add(emailError);
        }

        String passwordError = PasswordValidator.validate(request.password());
        if (passwordError != null) {
            errori.add(passwordError);
        }

        String nomeError = InputValidator.validateNotBlank(request.nome(), "Il nome");
        if (nomeError != null) {
            errori.add(nomeError);
        }

        String cognomeError = InputValidator.validateNotBlank(request.cognome(), "Il cognome");
        if (cognomeError != null) {
            errori.add(cognomeError);
        }

        return errori;
    }
}
