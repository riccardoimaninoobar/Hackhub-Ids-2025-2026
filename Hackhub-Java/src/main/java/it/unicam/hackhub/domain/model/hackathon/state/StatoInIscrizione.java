package it.unicam.hackhub.domain.model.hackathon.state;

import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.hackathon.Hackathon;

public class StatoInIscrizione implements StatoHackathon {

    @Override
    public void iscriviTeam(Hackathon h, Team t) {
        h.aggiungiTeam(t);
    }

    @Override
    public String getNomeStato() {
        return "In iscrizione";
    }
}