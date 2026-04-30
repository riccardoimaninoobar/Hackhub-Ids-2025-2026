package it.unicam.hackhub.domain.model;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name ="hackathons")
public class Hackathon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String nome;
    @Column(length = 2000)
    private String regolamento;

    private LocalDate scadenzaIscrizioni;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private String luogo;
    private Integer dimMaxTeam;
    // relazioni con Utente
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organizzatore_id")
    private Utente organizzatore;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "hackathon_mentori", // Crea la tabella ponte
            joinColumns = @JoinColumn(name = "hackathon_id"),
            inverseJoinColumns = @JoinColumn(name = "utente_id")
    )
    private Set<Utente> mentori = new HashSet<>();;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "giudice_id")
    private Utente giudice;
    @Convert(converter = it.unicam.hackhub.infrastructure.persistence.StatoHackathonConverter.class)
    @Column(name = "stato")
    private StatoHackathon stato;
    private BigDecimal premioImporto;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "hackathon_team", // Crea la tabella ponte
            joinColumns = @JoinColumn(name = "hackathon_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private Set<Team> teamPartecipanti = new HashSet<>();
    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Sottomissione> sottomissioni = new HashSet<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_vincente_id")
    private Team teamVincente;

    protected Hackathon() {}
    Hackathon(String nome, String regolamento, LocalDate scadenzaIscrizioni, LocalDate dataInizio,
              LocalDate dataFine, String luogo, Integer dimMaxTeam, Utente o, Utente g, Set<Utente> m, BigDecimal premioImporto) {
        this.nome = nome;
        this.regolamento = regolamento;
        this.scadenzaIscrizioni = scadenzaIscrizioni;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.luogo = luogo;
        this.dimMaxTeam = dimMaxTeam;
        this.organizzatore = o;
        this.giudice = g;
        if (m != null) {
            this.mentori.addAll(m);
        }
        this.stato = new StatoInIscrizione();
        this.premioImporto = premioImporto;
    }
    public void aggiungiMentore(Utente m) {
        if (m != null) {
            this.mentori.add(m);
        }
    }

    @PostLoad
    public void onLoad() {
        this.aggiornaStato();
    }

    public Long getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }

    public Utente getOrganizzatore() {
        return organizzatore;
    }

    public double getPremioInDenaro() {
        return premioImporto != null ? premioImporto.doubleValue() : 0.0;
    }

    public Set<Sottomissione> getSottomissioni() {
    return new HashSet<>(sottomissioni); // copia difensiva
    }

    public boolean isOrganizzatore(Utente u) {
        if(u == null) {
            throw new IllegalArgumentException("utente nullo");
        }
        return this.organizzatore.equals(u);
    }
    public boolean isGiudice(Utente u) {
        if(u == null) {
            throw new IllegalArgumentException("utente nullo");
        }
        return this.giudice.equals(u);
    }

    public Set<Utente> getMentori() {
        return new HashSet<>(mentori); // copia difensiva
    }

    public boolean isMentore(Utente u) {
        if (u == null)  {
            throw new IllegalArgumentException("utente nullo");
        }
        return mentori.contains(u);
    }

    public boolean utenteMembroStaff(Utente u) {
        if(u == null) {
            throw new IllegalArgumentException("utente nullo");
        }
        return (isOrganizzatore(u) || isGiudice(u) || mentori.contains(u));
    }
    public boolean utentePartecipante(Utente u) {
        if(u == null) {
            throw new IllegalArgumentException("utente nullo");
        }
        for(Team t : teamPartecipanti) {
            if(t.isMembro(u)) {
                return true;
            }
        }
        return false;
    }

    public StatoHackathon getStato() {
        return this.stato;
    }
    public void setStato(StatoHackathon stato) {
        this.stato = stato;
    }

    public void iscriviTeam(Team t) {
        // Delega allo stato: nel diagramma h chiama richiediIscrizioneTeam(h, t) sullo stato
        this.aggiornaStato();
        this.stato.iscriviTeam(this, t);
    }

    public void caricaSottomissione(Sottomissione s) {
        this.aggiornaStato(); // Controlla se l'hackathon è terminato (passando a valutazione)
        this.stato.caricaSottomissione(this, s);
    }

    public void aggiungiTeam(Team team) {
        if (team == null) {
            throw new IllegalArgumentException("team nullo");
        }
        this.teamPartecipanti.add(team);
        team.addHackathon(this);
    }

    // In Hackathon.java
    public void aggiungiSottomissione(Sottomissione sottomissione) {
        if (sottomissione == null || !teamPartecipanti.contains(sottomissione.getTeam())) {
            throw new IllegalArgumentException("Sottomissione non valida per questo hackathon");
        }
        this.sottomissioni.add(sottomissione);
        sottomissione.setHackathon(this);
    }

    /**
     * Verifica le scadenze temporali e aggiorna lo stato dell'Hackathon se necessario.
     */
    // In Hackathon.java
    public void aggiornaStato() {
        LocalDate oggi = LocalDate.now();

        // Controlliamo che la data di scadenza non sia null prima del confronto
        if (this.stato instanceof StatoInIscrizione && scadenzaIscrizioni != null && oggi.isAfter(scadenzaIscrizioni)) {
            this.setStato(new StatoInCorso());
        }

        // Controlliamo che la data di fine non sia null prima del confronto
        if (this.stato instanceof StatoInCorso && dataFine != null && oggi.isAfter(dataFine)) {
            this.setStato(new StatoInValutazione());
        }
    }

    public Team getTeamVincente() {
        return teamVincente;
    }

    public void setTeamVincente(Team teamVincente) {
        this.teamVincente = teamVincente;
    }

    public String getLuogo() { return luogo; }
    public LocalDate getDataInizio() { return dataInizio; }
    public LocalDate getDataFine() { return dataFine; }
    public LocalDate getScadenzaIscrizioni() { return scadenzaIscrizioni; }
    public Set<Team> getTeamPartecipanti() { return teamPartecipanti; }

}
