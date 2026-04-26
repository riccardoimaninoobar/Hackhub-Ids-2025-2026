package it.unicam.hackhub.infrastructure.persistence;

import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.repository.TeamRepository;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryTeamRepository
        extends InMemoryRepository<Team, String>
        implements TeamRepository {

    @Override
    protected String getId(Team team) {
        return team.getName(); // o getId()
    }
}