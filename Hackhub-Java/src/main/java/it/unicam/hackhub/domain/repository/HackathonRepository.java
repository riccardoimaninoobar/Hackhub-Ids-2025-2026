package it.unicam.hackhub.domain.repository;

import it.unicam.hackhub.domain.model.Hackathon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface HackathonRepository extends JpaRepository<Hackathon, Long> {
        Optional<Hackathon> findByNome(String nome);
        boolean existsByNome(String nome);
}
