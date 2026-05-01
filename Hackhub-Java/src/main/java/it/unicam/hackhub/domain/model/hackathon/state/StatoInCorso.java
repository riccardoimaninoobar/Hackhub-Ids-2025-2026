package it.unicam.hackhub.domain.model.hackathon.state;

import it.unicam.hackhub.domain.model.Sottomissione;
import it.unicam.hackhub.domain.model.hackathon.Hackathon;

public class StatoInCorso implements StatoHackathon {

    @Override
    public void caricaSottomissione(Hackathon h, Sottomissione s) {
        // Ramo [else] del Sequence Diagram: delega il salvataggio interno all'Hackathon
        h.aggiungiSottomissione(s);
    }

    @Override
    public String getNomeStato() {
        return "In corso";
    }
}