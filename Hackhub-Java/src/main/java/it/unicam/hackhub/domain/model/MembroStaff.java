package it.unicam.hackhub.domain.model;

public abstract class MembroStaff {
    private final Utente utente; // Il riferimento "ricopre"

    protected MembroStaff(Utente u) {
        if (u == null) throw new IllegalArgumentException("Un ruolo deve essere associato a un utente.");
        this.utente = u;
    }

    public Utente getUtente() { return utente; }
}