package it.unicam.hackhub.infrastructure.persistence;

import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.UtenteRepository;

public class InMemoryUtenteRepository
        extends InMemoryRepository<Utente, String>
        implements UtenteRepository {

    @Override
    protected String getId(Utente utente) {
        return utente.getNome(); // o getId()
    }
}