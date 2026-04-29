package it.unicam.hackhub.domain.model;

public class StatoInValutazione implements StatoHackathon {

    @Override
    public void valutaSottomissione(Hackathon h, Sottomissione s, int punteggio) {
        // Ramo di successo: delega il salvataggio del voto all'Hackathon
        s.setPunteggio(punteggio);
    }

    @Override
    public void proclamaVincitore(Hackathon h) {
        // Logica per calcolare il vincitore (es. h.calcolaTeamVincitore())

        // Questo è il momento in cui avviene la TRANSIZIONE FINALE
        h.setStato(new StatoConcluso());
    }

    @Override
    public String getNomeStato() {
        return "In valutazione";
    }
}