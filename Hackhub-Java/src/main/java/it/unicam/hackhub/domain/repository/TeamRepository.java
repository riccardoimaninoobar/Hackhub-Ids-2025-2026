package it.unicam.hackhub.domain.repository;

import it.unicam.hackhub.domain.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByNome(String nome);
    boolean existsByNome(String nome);
}

