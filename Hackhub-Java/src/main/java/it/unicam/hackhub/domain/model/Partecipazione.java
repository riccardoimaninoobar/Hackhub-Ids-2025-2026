package it.unicam.hackhub.domain.model;

import it.unicam.hackhub.domain.model.hackathon.Hackathon;
import jakarta.persistence.*;

@Entity
public class Partecipazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "hackathon_id")
    private Hackathon hackathon;

    // Il famoso flag spostato qui!
    private boolean squalificato = false;

    protected Partecipazione() {}

    public Partecipazione(Team team, Hackathon hackathon) {
        this.team = team;
        this.hackathon = hackathon;
    }

    public void setSqualificato(boolean squalificato) {
        this.squalificato = squalificato;
    }

    public Team getTeam() {
        return this.team;
    }

    public Hackathon getHackathon() {
        return this.hackathon;
    }

    public boolean isSqualificato() {
        return this.squalificato;
    }
}