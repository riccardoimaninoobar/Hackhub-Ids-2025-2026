package it.unicam.hackhub.domain.model;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Hackathon {
    private final String nome;
    private final String regolamento;
    private final LocalDate scadenzaIscrizioni;
    private final LocalDate dataInizio;
    private final LocalDate dataFine;
    private final String luogo;
    private final Integer dimMaxTeam;
    private final Organizzatore organizzatore;
    private final List<Mentore> mentori = new ArrayList<>();;
    private final Giudice giudice;
    private String stato;
    private BigDecimal premioImporto;

    Hackathon(String nome, String regolamento, LocalDate scadenzaIscrizioni, LocalDate dataInizio,
              LocalDate dataFine, String luogo, Integer dimMaxTeam, Organizzatore o, Giudice g, List<Mentore> m, BigDecimal premioImporto) {
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
    public void aggiungiMentore(Mentore m) {
        if (m != null) {
            this.mentori.add(m);
        }
    }
    public String getNome() {
        return nome;
    }

    public Organizzatore getOrganizzatore() {
        return organizzatore;
    }
}



