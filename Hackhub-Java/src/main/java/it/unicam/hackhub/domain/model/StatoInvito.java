package it.unicam.hackhub.domain.model;

public interface StatoInvito {
    void accetta(Invito invito);
    void rifiuta(Invito invito);
    String getStato(); // Restituisce la stringa (es. "IN_ATTESA")
}