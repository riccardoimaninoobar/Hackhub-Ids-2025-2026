package it.unicam.hackhub.domain.repository;

import it.unicam.hackhub.domain.model.Notifica;
import it.unicam.hackhub.domain.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificaRepository extends JpaRepository<Notifica, Long> {
    List<Notifica> findByDestinatario(Utente utente);
}