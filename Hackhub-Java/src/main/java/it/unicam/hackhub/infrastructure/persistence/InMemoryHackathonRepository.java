package it.unicam.hackhub.infrastructure.persistence;

import it.unicam.hackhub.domain.model.Hackathon;
import it.unicam.hackhub.domain.repository.HackathonRepository;

public class InMemoryHackathonRepository
        extends InMemoryRepository<Hackathon, String>
        implements HackathonRepository {

    @Override
    protected String getId(Hackathon hackathon) {
        return hackathon.getNome(); // o getId()
    }
}