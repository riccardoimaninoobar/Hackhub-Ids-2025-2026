package it.unicam.hackhub.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entità che rappresenta un team di partecipanti a un hackathon.
 */
public class Team {

    private Long id;
    private String nome;
    private LocalDateTime dataCreazione;
    private Utente creatore;
    private List<Utente> membri;

    /**
     * Crea un nuovo team con il nome e il creatore specificati.
     * Il creatore viene automaticamente aggiunto come primo membro.
     */
    public Team(String nome, Utente creatore) {
        this.nome = nome;
        this.creatore = creatore;
        this.dataCreazione = LocalDateTime.now();
        this.membri = new ArrayList<>();
        this.membri.add(creatore);
        creatore.setTeam(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    public Utente getCreatore() {
        return creatore;
    }

    public List<Utente> getMembri() {
        return Collections.unmodifiableList(membri);
    }

    /**
     * Aggiunge un membro al team.
     * @param utente l'utente da aggiungere
     * @return true se l'utente è stato aggiunto, false se era già membro
     */
    public boolean aggiungiMembro(Utente utente) {
        if (utente == null || membri.contains(utente)) {
            return false;
        }
        if (utente.getTeam() != null) {
            throw new IllegalStateException("L'utente " + utente.getUsername() + " appartiene già a un team");
        }
        membri.add(utente);
        utente.setTeam(this);
        return true;
    }

    /**
     * Rimuove un membro dal team.
     * Il creatore non può essere rimosso.
     */
    public boolean rimuoviMembro(Utente utente) {
        if (utente == null || utente.equals(creatore)) {
            return false;
        }
        if (membri.remove(utente)) {
            utente.setTeam(null);
            return true;
        }
        return false;
    }

    public int getNumerMembri() {
        return membri.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(id, team.id) && Objects.equals(nome, team.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome);
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", creatore=" + creatore.getUsername() +
                ", membri=" + membri.size() +
                '}';
    }
}
