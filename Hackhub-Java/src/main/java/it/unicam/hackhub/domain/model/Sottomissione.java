package it.unicam.hackhub.domain.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
@Entity
@Table(name = "sottomissioni")
public class Sottomissione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nomeFile;
    private String link; // o percorso file
    @Column(nullable = false)
    private LocalDateTime dataCaricamento;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;
    private int punteggio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id")
    private Hackathon hackathon;

    protected Sottomissione() {}

    public Sottomissione(String nomeFile, String link, Team team) {
        this.nomeFile = nomeFile;
        this.link = link;
        this.dataCaricamento = LocalDateTime.now();
        this.team = team;
        this.punteggio = 0;
    }

    // getter
    public Long getId() { return id; }
    public String getNomeFile() { return nomeFile; }
    public String getLink() { return link; }
    public LocalDateTime getDataCaricamento() { return dataCaricamento; }
    public Team getTeam() { return team; }

    public int getPunteggio() {
        return this.punteggio;
    }

    public void setPunteggio(int punteggio) {
        this.punteggio = punteggio;
    }

    public Hackathon getHackathon() { return hackathon; }

    public void setHackathon(Hackathon hackathon) { this.hackathon = hackathon; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sottomissione that = (Sottomissione) o;
        return Objects.equals(nomeFile, that.nomeFile) &&
               Objects.equals(team, that.team);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nomeFile, team);
    }
}
