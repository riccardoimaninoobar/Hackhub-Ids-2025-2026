package it.unicam.hackhub.repository;

import it.unicam.hackhub.domain.model.Utente;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Repository in memoria per la gestione degli utenti.
 * Simula un database con collezioni Java.
 */
public class UtenteRepository {

    private final Map<Long, Utente> utenti = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Salva un utente nel repository.
     * Se l'utente non ha un ID, ne viene generato uno nuovo.
     * @param utente l'utente da salvare
     * @return l'utente salvato con l'ID assegnato
     */
    public Utente save(Utente utente) {
        if (utente.getId() == null) {
            utente.setId(idGenerator.getAndIncrement());
        }
        utenti.put(utente.getId(), utente);
        return utente;
    }

    /**
     * Trova un utente per ID.
     */
    public Optional<Utente> findById(Long id) {
        return Optional.ofNullable(utenti.get(id));
    }

    /**
     * Trova un utente per username.
     */
    public Optional<Utente> findByUsername(String username) {
        return utenti.values().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    /**
     * Trova un utente per email.
     */
    public Optional<Utente> findByEmail(String email) {
        return utenti.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    /**
     * Verifica se esiste un utente con lo username specificato.
     */
    public boolean existsByUsername(String username) {
        return utenti.values().stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    /**
     * Verifica se esiste un utente con l'email specificata.
     */
    public boolean existsByEmail(String email) {
        return utenti.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    /**
     * Restituisce tutti gli utenti.
     */
    public List<Utente> findAll() {
        return new ArrayList<>(utenti.values());
    }

    /**
     * Conta il numero di utenti registrati.
     */
    public long count() {
        return utenti.size();
    }

    /**
     * Elimina un utente per ID.
     */
    public boolean deleteById(Long id) {
        return utenti.remove(id) != null;
    }

    /**
     * Elimina tutti gli utenti.
     */
    public void deleteAll() {
        utenti.clear();
    }
}
