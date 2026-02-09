package it.unicam.hackhub.repository;

import it.unicam.hackhub.domain.model.Team;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Repository in memoria per la gestione dei team.
 */
public class TeamRepository {

    private final Map<Long, Team> teams = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Salva un team nel repository.
     */
    public Team save(Team team) {
        if (team.getId() == null) {
            team.setId(idGenerator.getAndIncrement());
        }
        teams.put(team.getId(), team);
        return team;
    }

    /**
     * Trova un team per ID.
     */
    public Optional<Team> findById(Long id) {
        return Optional.ofNullable(teams.get(id));
    }

    /**
     * Trova un team per nome.
     */
    public Optional<Team> findByNome(String nome) {
        return teams.values().stream()
                .filter(t -> t.getNome().equalsIgnoreCase(nome))
                .findFirst();
    }

    /**
     * Verifica se esiste un team con il nome specificato.
     */
    public boolean existsByNome(String nome) {
        return teams.values().stream()
                .anyMatch(t -> t.getNome().equalsIgnoreCase(nome));
    }

    /**
     * Restituisce tutti i team.
     */
    public List<Team> findAll() {
        return new ArrayList<>(teams.values());
    }

    /**
     * Conta il numero di team.
     */
    public long count() {
        return teams.size();
    }

    /**
     * Elimina un team per ID.
     */
    public boolean deleteById(Long id) {
        return teams.remove(id) != null;
    }
}
