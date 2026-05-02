package it.unicam.hackhub.domain.model;

import it.unicam.hackhub.domain.model.hackathon.Hackathon;
import it.unicam.hackhub.domain.model.hackathon.state.StatoInCorso;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.Objects;
import java.util.stream.Collectors;
@Entity
@Table(name = "teams")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String nome;
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Utente> members;
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Partecipazione> partecipazioni = new HashSet<>();
    private String datiBancari;

    protected Team() {}
    public Team(String name) {
        this.nome = name;
        this.members = new HashSet<>();
    }

    public Long getId() { return id; }

    public String getDatiBancari() {
        return datiBancari != null ? datiBancari : "Dati non inseriti";
    }

    public void setDatiBancari(String datiBancari) {
        this.datiBancari = datiBancari;
    }

    public String getNome() {
        return nome;
    }

    public void setName(String nome) {
        this.nome = nome;
    }

    public Set<Utente> getMembers() {
        return members;
    }

    public void aggiungiMembro(Utente member) {
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
        return Objects.equals(nome, team.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }

    public boolean isMembro(Utente u) {
        return this.members.contains(u);
    }
    public Set<Hackathon> getHackathons() {
        return partecipazioni.stream()
                .map(Partecipazione::getHackathon)
                .collect(Collectors.toSet());
    }

    public Set<Hackathon> getHackathonInCorso() {
        return partecipazioni.stream()
                .map(Partecipazione::getHackathon)
                .filter(h -> h.getStato() instanceof StatoInCorso)
                .collect(Collectors.toSet());
    }

    // NUOVO METODO (sostituisce addHackathon)
    public void addPartecipazione(Partecipazione p) {
        this.partecipazioni.add(p);
    }

}