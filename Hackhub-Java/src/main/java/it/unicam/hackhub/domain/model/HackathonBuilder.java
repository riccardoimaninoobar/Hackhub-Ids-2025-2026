package it.unicam.hackhub.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HackathonBuilder {
    private String nome;
    private String regolamento;
    private LocalDate scadenzaIscrizioni;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private String luogo;
    private Integer dimMaxTeam;
    private Organizzatore organizzatore;
    private List<Mentore> mentori = new ArrayList<>();;
    private Giudice giudice;
    private BigDecimal premioImporto;

    public HackathonBuilder() {
        this.mentori = new ArrayList<>();
    }

    public HackathonBuilder assegnaNome(String nome) {
        this.nome = nome;
        return this;
    }
    public HackathonBuilder assegnaRegolamento(String regolamento) {
        this.regolamento = regolamento;
        return this;
    }
    public HackathonBuilder assegnaScadenza(LocalDate scadenzaIscrizioni) {
        this.scadenzaIscrizioni = scadenzaIscrizioni;
        return this;
    }
    public HackathonBuilder assegnaDataInizio(LocalDate dataInizio) {
        this.dataInizio = dataInizio;
        return this;
    }
    public HackathonBuilder assegnaDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
        return this;
    }
    public HackathonBuilder assegnaLuogo(String luogo) {
        this.luogo = luogo;
        return this;
    }
    public HackathonBuilder assegnaDimMaxTeam(Integer dimMaxTeam) {
        this.dimMaxTeam = dimMaxTeam;
        return this;
    }
    public HackathonBuilder assegnaOrganizzatore(Organizzatore organizzatore) {
        this.organizzatore = organizzatore;
        return this;
    }
    public HackathonBuilder assegnaGiudice(Giudice giudice) {
        this.giudice = giudice;
        return this;
    }
    public HackathonBuilder assegnaMentore(Mentore mentore) {
        this.mentori.add(mentore);
        return this;
    }
    public Hackathon build() {
        if (nome == null || nome.isEmpty()) {
            throw new IllegalStateException("Il nome dell'Hackathon è obbligatorio.");
        }
        if (organizzatore == null) {
            throw new IllegalStateException("Un Hackathon deve avere un organizzatore.");
        }
        if (dataInizio != null && dataFine != null && dataFine.isBefore(dataInizio)) {
            throw new IllegalStateException("La data di fine non può essere precedente all'inizio.");
        }
        return new Hackathon(
                nome, regolamento, scadenzaIscrizioni, dataInizio,
                dataFine, luogo, dimMaxTeam, organizzatore, mentori, giudice
        );
    }
}