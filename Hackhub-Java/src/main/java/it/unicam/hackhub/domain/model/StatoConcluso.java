package it.unicam.hackhub.domain.model;

public class StatoConcluso implements StatoHackathon {

    // Nessuna azione è permessa. Se un Handler chiama h.richiediIscrizioneTeam(),
    // scatterà il default() dell'interfaccia e lancerà l'IllegalStateException.

    @Override
    public String getNomeStato() {
        return "Concluso";
    }
}