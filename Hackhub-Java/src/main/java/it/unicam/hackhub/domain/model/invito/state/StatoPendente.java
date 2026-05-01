package it.unicam.hackhub.domain.model.invito.state;

import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.invito.Invito;

public class StatoPendente implements StatoInvito {

    @Override
    public void accetta(Invito invito) {
        // 1. getTeamMittente()
        Team team = invito.getTeamMittente();

        // 2. aggiungiMembro(utente) -> che al suo interno farà setTeam(team) su Utente
        team.aggiungiMembro(invito.getInvitato()); // Assumo tu abbia aggiungiMembro o aggiungiMembro in Team

        // 3. <<create>> StatoAccettato e setState(accettato)
        invito.setStato(new StatoAccettato());
    }

    @Override
    public void rifiuta(Invito invito) {
        invito.setStato(new StatoRifiutato());
    }

    @Override
    public String getStato() { return "IN_ATTESA"; }
}