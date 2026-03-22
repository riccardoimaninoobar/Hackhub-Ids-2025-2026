package it.unicam.hackhub.domain.model;

public class StatoAccettato implements StatoInvito {

    @Override
    public void accetta(Invito invito) {
        throw new IllegalStateException("Errore: L'invito è già stato accettato.");
    }

    @Override
    public void rifiuta(Invito invito) {
        throw new IllegalStateException("Errore: Non puoi rifiutare un invito già accettato.");
    }

    @Override
    public String getStato() {
        return "ACCETTATO";
    }
}