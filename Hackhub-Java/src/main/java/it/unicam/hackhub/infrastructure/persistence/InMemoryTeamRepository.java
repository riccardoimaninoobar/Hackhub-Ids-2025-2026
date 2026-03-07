package it.unicam.hackhub.infrastructure.persistence;

import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.repository.TeamRepository;
public class InMemoryTeamRepository
        extends InMemoryRepository<Team, String>
        implements TeamRepository {

    @Override
    protected String getId(Team team) {
        return team.getName(); // o getId()
    }
}