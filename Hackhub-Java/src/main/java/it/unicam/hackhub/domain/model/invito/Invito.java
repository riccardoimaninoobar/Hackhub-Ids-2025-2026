package it.unicam.hackhub.domain.model.invito;

import it.unicam.hackhub.domain.model.invito.state.StatoInvito;
import it.unicam.hackhub.domain.model.invito.state.StatoPendente;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import jakarta.persistence.*;

@Entity
@Table(name = "invito")
public class Invito {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitato_id", nullable = false)
    private Utente invitato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team teamMittente;
    @Convert(converter = it.unicam.hackhub.infrastructure.persistence.StatoInvitoConverter.class)
    @Column(name = "stato")
    private StatoInvito stato;

    protected Invito() {}

    public Invito(Utente invitato, Team teamMittente) {
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

    public Long getId() { return id; }
    public Utente getInvitato() { return invitato; }
    public Team getTeamMittente() { return teamMittente; }
    public StatoInvito getStato() { return stato; } // Ritorna l'oggetto Stato

    // Helper per recuperare agilmente la stringa
    public String getNomeStato() { return this.stato.getStato(); }
}