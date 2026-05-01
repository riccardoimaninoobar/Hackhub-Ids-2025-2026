package it.unicam.hackhub.domain.model.hackathon.state;

import it.unicam.hackhub.domain.model.Sottomissione;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.hackathon.Hackathon;

public interface StatoHackathon {

    // --- Metodi già visti ---
    default void iscriviTeam(Hackathon h, Team t) {
        throw new IllegalStateException("Operazione non consentita: l'Hackathon non è in fase di iscrizione.");
    }

    default void caricaSottomissione(Hackathon h, Sottomissione s) {
        throw new IllegalStateException("Operazione non consentita: l'Hackathon non è attualmente in corso.");
    }

    default void valutaSottomissione(Hackathon h, Sottomissione s, int punteggio) {
        throw new IllegalStateException("Operazione non consentita: l'Hackathon non è in fase di valutazione.");
    }

    default void proclamaVincitore(Hackathon h) {
        throw new IllegalStateException("Operazione non consentita: non è possibile proclamare il vincitore in questo stato.");
    }

    // --- METODO ASTRATTO ---
    String getNomeStato();
}