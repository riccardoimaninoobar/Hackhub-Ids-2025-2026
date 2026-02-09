package it.unicam.hackhub.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entità che rappresenta un utente della piattaforma HackHub.
 */
public class Utente {

    private Long id;
    private String username;
    private String email;
    private String password;
    private String nome;
    private String cognome;
    private LocalDateTime dataRegistrazione;
    private Team team;

    /**
     * Costruttore completo per la creazione di un nuovo utente.
     */
    public Utente(String username, String email, String password, String nome, String cognome) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.dataRegistrazione = LocalDateTime.now();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public LocalDateTime getDataRegistrazione() {
        return dataRegistrazione;
    }

    public Team getTeam() {
        return team;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getNomeCompleto() {
        return nome + " " + cognome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utente utente = (Utente) o;
        return Objects.equals(id, utente.id) &&
               Objects.equals(username, utente.username) &&
               Objects.equals(email, utente.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email);
    }

    @Override
    public String toString() {
        return "Utente{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", dataRegistrazione=" + dataRegistrazione +
                ", team=" + (team != null ? team.getNome() : "nessuno") +
                '}';
    }
}
