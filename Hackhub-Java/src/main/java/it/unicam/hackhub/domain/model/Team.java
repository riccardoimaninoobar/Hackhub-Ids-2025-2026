package it.unicam.hackhub.domain.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;

public class Team {
    private String name;
    private final Set<Utente> members;
    private final Set<Hackathon> hackathons;
    private String datiBancari;

    public Team(String name) {
        this.name = name;
        this.members = new HashSet<>();
        this. hackathons = new HashSet<>();
    }

     public String getDatiBancari() {
        return datiBancari != null ? datiBancari : "Dati non inseriti";
    }

    public void setDatiBancari(String datiBancari) {
        this.datiBancari = datiBancari;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Utente> getMembers() {
        return members;
    }

    public void addMember(Utente member) {
        if (!this.members.contains(member)) {
            this.members.add(member);
            member.setTeam(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(name, team.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public boolean isMembro(Utente u) {
        return this.members.contains(u);
    }
    public Set<Hackathon> getHackathons() {
        return new HashSet<>(hackathons); // Copia difensiva
    }

    public Set<Hackathon> getHackathonInCorso() {
        return this.hackathons.stream()
                // Sostituisci la condizione con il modo in cui gestisci lo stato
                // Es. h.getStato().equals("In corso") oppure (h.getStatoCorrente() instanceof StatoInCorso)
                .filter(h -> h.getStato().equals("In corso"))
                .collect(Collectors.toSet());
    }

    public void addHackathon(Hackathon hackathon) {
        this.hackathons.add(hackathon);
    }

}