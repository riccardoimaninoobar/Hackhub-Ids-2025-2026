package it.unicam.hackhub.domain.repository;

import it.unicam.hackhub.domain.model.invito.Invito;
import it.unicam.hackhub.domain.model.invito.state.StatoInvito;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitoRepository extends JpaRepository<Invito, Long> {
    boolean existsByInvitatoAndTeamMittenteAndStato(Utente invitato, Team team, StatoInvito stato);
    List<Invito> findByInvitatoAndStato(Utente utente, StatoInvito stato);
}
