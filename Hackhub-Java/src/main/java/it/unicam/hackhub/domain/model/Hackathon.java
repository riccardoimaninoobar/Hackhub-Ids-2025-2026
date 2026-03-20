package it.unicam.hackhub.domain.model;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Hackathon {
    private final String nome;
    private final String regolamento;
    private final LocalDate scadenzaIscrizioni;
    private final LocalDate dataInizio;
    private final LocalDate dataFine;
    private final String luogo;
    private final Integer dimMaxTeam;
    private final Utente organizzatore;
    private final Set<Utente> mentori = new HashSet<>();;
    private final Utente giudice;
    private String stato;
    private BigDecimal premioImporto;
    private Set<Team> teamPartecipanti = new HashSet<>();
    private final Set<Sottomissione> sottomissioni = new HashSet<>();

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
        this.stato = "In iscrizione";
        this.premioImporto = premioImporto;
    }
    public void aggiungiMentore(Utente m) {
        if (m != null) {
            this.mentori.add(m);
        }
    }
    public String getNome() {
        return nome;
    }

    public Utente getOrganizzatore() {
        return organizzatore;
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

    public String getStato() {
    return stato;
    }

    public void aggiungiTeam(Team team) {
        if (team == null) {
            throw new IllegalArgumentException("team nullo");
        }
        this.teamPartecipanti.add(team);
    }

    public void aggiungiSottomissione(Sottomissione sottomissione) {
        if (sottomissione == null || utentePartecipante(sottomissione.getTeam().getMembers().iterator().next())) {
        throw new IllegalArgumentException("Sottomissione non valida per questo hackathon");
        }
        this.sottomissioni.add(sottomissione);
    }
}



