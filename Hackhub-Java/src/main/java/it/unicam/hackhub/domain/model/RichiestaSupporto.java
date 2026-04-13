package it.unicam.hackhub.domain.model;

import java.util.UUID;

public class RichiestaSupporto {
    private final String id;
    private final Team team;
    private final Hackathon hackathon;
    private final String descrizione;

    public RichiestaSupporto(Team team, Hackathon hackathon, String descrizione) {
        this.id = UUID.randomUUID().toString();
        this.team = team;
        this.hackathon = hackathon;
        this.descrizione = descrizione;
    }

    public String getId() { return id; }
    public Team getTeam() { return team; }
    public Hackathon getHackathon() { return hackathon; }
    public String getDescrizione() { return descrizione; }
}