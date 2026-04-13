package it.unicam.hackhub.domain.model;

public class StatoInIscrizione implements StatoHackathon {

    @Override
    public void iscriviTeam(Hackathon h, Team t) {
        // Ramo [else] del Sequence Diagram: delega il salvataggio interno all'Hackathon
        h.aggiungiTeam(t);
    }

    @Override
    public String getNomeStato() {
        return "In iscrizione";
    }
}