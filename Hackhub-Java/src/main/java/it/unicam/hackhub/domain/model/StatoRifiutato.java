package it.unicam.hackhub.domain.model;

public class StatoRifiutato implements StatoInvito {

    @Override
    public void accetta(Invito invito) {
        throw new IllegalStateException("Errore: Non puoi accettare un invito che avevi già rifiutato.");
    }

    @Override
    public void rifiuta(Invito invito) {
        throw new IllegalStateException("Errore: L'invito è già stato rifiutato.");
    }

    @Override
    public String getStato() {
        return "RIFIUTATO";
    }
}