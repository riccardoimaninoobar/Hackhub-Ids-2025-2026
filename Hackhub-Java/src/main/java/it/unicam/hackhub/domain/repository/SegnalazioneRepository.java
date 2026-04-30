package it.unicam.hackhub.domain.repository;

import it.unicam.hackhub.domain.model.SegnalazioneViolazione;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SegnalazioneRepository extends JpaRepository<SegnalazioneViolazione, Long> {
}
