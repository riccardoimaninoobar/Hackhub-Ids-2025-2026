package it.unicam.hackhub.domain.model;

import jakarta.persistence.*; // ATTENZIONE: Usa jakarta, non javax!
import java.util.Objects;

@Entity // 1. Indica che questa è una tabella del DB
@Table(name = "utenti") // (Opzionale) Forza il nome della tabella al plurale
public class Utente {

    @Id // 2. Questa è la chiave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // 4. Mappatura della relazione con il Team
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id") // Crea una colonna "team_id" nella tabella utenti
    private Team team;

    // 3. COSTRUTTORE VUOTO OBBLIGATORIO PER JPA (può essere protected)
    protected Utente() {}

    // Il tuo costruttore originale rimane intatto (l'id non si passa, lo genera il DB)
    public Utente(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // --- GETTER E SETTER ---
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public boolean verificaPassword(String password) {
        return this.password.equals(password);
    }

    public boolean hasTeam() {
        return this.team != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utente user = (Utente) o;
        return Objects.equals(username, user.username);
    }
}