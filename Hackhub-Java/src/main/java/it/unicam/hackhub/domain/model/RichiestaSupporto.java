package it.unicam.hackhub.domain.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
@Entity
@Table(name = "richiesta_supporto")
public class RichiestaSupporto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathon hackathon;
    @Column(nullable = false, length = 1000)
    private String descrizione;
    @Column(length = 1000)
    private String risposta;
    private LocalDate dataCall;
    private LocalTime oraCall;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoRichiestaSupporto stato;

    protected RichiestaSupporto() {}

    public RichiestaSupporto(Team team, Hackathon hackathon, String descrizione) {
        this.team = team;
        this.hackathon = hackathon;
        this.descrizione = descrizione;
        this.stato = StatoRichiestaSupporto.APERTA;
    }

    public Long getId() { return id; }
    public Team getTeam() { return team; }
    public Hackathon getHackathon() { return hackathon; }
    public String getDescrizione() { return descrizione; }
    public String getRisposta() { return risposta; }
    public LocalDate getDataCall() { return dataCall; }
    public LocalTime getOraCall() { return oraCall; }
    public StatoRichiestaSupporto getStato() { return stato; }

    public boolean isAperta() {
        return stato == StatoRichiestaSupporto.APERTA || stato == StatoRichiestaSupporto.RISPOSTA_INSERITA;
    }

    public void aggiungiRisposta(String risposta) {
        if (risposta == null || risposta.trim().isEmpty()) {
            throw new IllegalArgumentException("La risposta non può essere vuota.");
        }
        this.risposta = risposta;
        this.stato = StatoRichiestaSupporto.RISPOSTA_INSERITA;
    }

    public void associaSlot(LocalDate data, LocalTime ora) {
        if (data == null || ora == null) {
            throw new IllegalArgumentException("Data e ora dello slot sono obbligatorie.");
        }
        this.dataCall = data;
        this.oraCall = ora;
        this.stato = StatoRichiestaSupporto.CHIUSA;
    }
}