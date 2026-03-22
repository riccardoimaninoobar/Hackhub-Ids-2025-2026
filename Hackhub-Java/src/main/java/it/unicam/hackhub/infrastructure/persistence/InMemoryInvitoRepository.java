package it.unicam.hackhub.infrastructure.persistence;

import it.unicam.hackhub.domain.model.Invito;
import it.unicam.hackhub.domain.model.StatoPendente;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.InvitoRepository;

import java.util.List;

public class InMemoryInvitoRepository
        extends InMemoryRepository<Invito, String>
        implements InvitoRepository {

    @Override
    protected String getId(Invito invito) {
        return invito.getId();
    }

    @Override
    public boolean existsActiveInvitation(Utente invitato, Team team, String stato) {
        for (Invito i : store.values()) {
            if (i.getInvitato().equals(invitato) &&
                    i.getTeam().equals(team) &&
                    i.getStato().equals(stato)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public List<Invito> findPending(Utente utente) {
        return store.values().stream()
                .filter(i -> i.getInvitato().equals(utente) && i.getStato() instanceof StatoPendente)
                .toList(); // Restituisce la lista (l'array Invito[] del diagramma)
    }
}