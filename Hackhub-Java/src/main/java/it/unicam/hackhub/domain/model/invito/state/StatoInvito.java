package it.unicam.hackhub.domain.model.invito.state;

import it.unicam.hackhub.domain.model.invito.Invito;

public interface StatoInvito {
    void accetta(Invito invito);
    void rifiuta(Invito invito);
    String getStato(); // Restituisce la stringa (es. "IN_ATTESA")
}