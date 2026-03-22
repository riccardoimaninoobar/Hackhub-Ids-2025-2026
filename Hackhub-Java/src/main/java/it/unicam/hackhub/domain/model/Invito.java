package it.unicam.hackhub.domain.model;

import java.util.UUID;

public class Invito {
    private final String id;
    private final Utente invitato;
    private final Team teamMittente;
    private StatoInvito stato; // Applicazione del Pattern State!

    public Invito(Utente invitato, Team teamMittente) {
        this.id = UUID.randomUUID().toString(); // Oppure senza UUID, come preferisci tu
        this.invitato = invitato;
        this.teamMittente = teamMittente;

        // Lo stato iniziale, secondo il pattern, è sempre Pendente
        this.stato = new StatoPendente();
    }

    // --- Metodi delegati allo Stato ---
    public void accetta() {
        this.stato.accetta(this);
    }

    public void rifiuta() {
        this.stato.rifiuta(this);
    }

    // --- Getter e Setter ---
    public void setStato(StatoInvito nuovoStato) {
        this.stato = nuovoStato;
    }

    public String getId() { return id; }
    public Utente getInvitato() { return invitato; }
    public Team getTeam() { return teamMittente; }
    public StatoInvito getStato() { return stato; } // Ritorna l'oggetto Stato

    // Helper per recuperare agilmente la stringa
    public String getNomeStato() { return this.stato.getStato(); }
}