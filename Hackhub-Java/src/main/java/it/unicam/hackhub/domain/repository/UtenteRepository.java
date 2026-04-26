package it.unicam.hackhub.domain.repository;

import it.unicam.hackhub.domain.model.Hackathon;
import it.unicam.hackhub.domain.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente, Long> {
    Optional<Utente> findByUsername(String nome);
    boolean existsByUsername(String nome);
}