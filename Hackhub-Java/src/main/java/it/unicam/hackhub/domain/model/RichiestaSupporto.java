package it.unicam.hackhub.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class RichiestaSupporto {
    private final String id;
    private final Team team;
    private final Hackathon hackathon;
    private final String descrizione;
    private String risposta;
    private LocalDate dataCall;
    private LocalTime oraCall;
    private StatoRichiestaSupporto stato;

    public RichiestaSupporto(Team team, Hackathon hackathon, String descrizione) {
        this.id = UUID.randomUUID().toString();
        this.team = team;
        this.hackathon = hackathon;
        this.descrizione = descrizione;
        this.stato = StatoRichiestaSupporto.APERTA;
    }

    public String getId() { return id; }
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