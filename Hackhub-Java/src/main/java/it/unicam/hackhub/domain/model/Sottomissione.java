package it.unicam.hackhub.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Sottomissione {
    private final String nomeFile;
    private final String link; // o percorso file
    private final LocalDateTime dataCaricamento;
    private final Team team;

    public Sottomissione(String nomeFile, String link, Team team) {
        this.nomeFile = nomeFile;
        this.link = link;
        this.dataCaricamento = LocalDateTime.now();
        this.team = team;
    }

    // getter
    public String getNomeFile() { return nomeFile; }
    public String getLink() { return link; }
    public LocalDateTime getDataCaricamento() { return dataCaricamento; }
    public Team getTeam() { return team; }

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
