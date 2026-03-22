package it.unicam.hackhub.domain.repository;

import it.unicam.hackhub.domain.model.Invito;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;

import java.util.List;

public interface InvitoRepository extends Repository<Invito, String> {
    // Metodo specifico richiesto dal Sequence Diagram
    boolean existsActiveInvitation(Utente invitato, Team team, String stato);
    List<Invito> findPending(Utente utente);
}
