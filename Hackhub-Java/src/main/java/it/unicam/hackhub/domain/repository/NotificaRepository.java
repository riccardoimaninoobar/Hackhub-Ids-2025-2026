package it.unicam.hackhub.domain.repository;

import it.unicam.hackhub.domain.model.Notifica;
import it.unicam.hackhub.domain.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificaRepository extends JpaRepository<Notifica, Long> {
    List<Notifica> findByDestinatario(Utente utente);
}