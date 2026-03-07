package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.domain.model.*;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreazioneHackathonHandler {

    private final HackathonRepository hackathonRepo;
    private final UtenteRepository utenteRepo;
    private HackathonBuilder currentBuilder;

    public CreazioneHackathonHandler(HackathonRepository hRepo, UtenteRepository uRepo) {
        this.hackathonRepo = hRepo;
        this.utenteRepo = uRepo;
    }

    // --- STEP 4: Verifica unicità del nome ---
    public boolean hackathonExists(String nome) {
        return hackathonRepo.findById(nome).isPresent();
    }

    // --- STEP 5: Crea Hackathon e associa l'Organizzatore ---
    public void creaHackathonBase(Utente organizzatore, String nome, String regolamento, 
                                  LocalDate scadenza, LocalDate inizio, LocalDate fine, 
                                  String luogo, Integer maxTeam, BigDecimal premio) {
        
        if (hackathonExists(nome)) {
            throw new IllegalArgumentException("Hackathon con questo nome già esistente.");
        }

        Organizzatore org = new Organizzatore(organizzatore);

        this.currentBuilder = new HackathonBuilder()
                .assegnaNome(nome)
                .assegnaRegolamento(regolamento)
                .assegnaScadenza(scadenza)
                .assegnaDataInizio(inizio)
                .assegnaDataFine(fine)
                .assegnaLuogo(luogo)
                .assegnaDimMaxTeam(maxTeam)
                .assegnaPremioImporto(premio)
                .assegnaOrganizzatore(org);
    }

    // --- STEP 8: Associa il Giudice ---
    public boolean assegnaGiudice(String idGiudice) {
        checkBuilder();
        var optUtente = utenteRepo.findById(idGiudice);
        if(optUtente.isEmpty()) {
            return false;
        }
        Utente u = optUtente.get();
        currentBuilder.assegnaGiudice(new Giudice(u));
        return true;
    }

    // --- STEP 11: Associa i Mentori ---
    public boolean assegnaMentore(String idMentore) {
        checkBuilder();
        var optUtente = utenteRepo.findById(idMentore);

        if (optUtente.isEmpty()) {
            return false; // il chiamante può chiedere di reinserire l'ID
        }
        Utente u = optUtente.get();
        currentBuilder.assegnaMentore(new Mentore(u));
        return true;
    }

    // --- STEP 12: Salva nel DB ---
    public void confermaCreazione() {
        checkBuilder();
        Hackathon nuovoHackathon = currentBuilder.build();
        hackathonRepo.save(nuovoHackathon);
        this.currentBuilder = null; // Pulisce la memoria
    }

    private void checkBuilder() {
        if (currentBuilder == null) {
            throw new IllegalStateException("Nessuna creazione in corso.");
        }
    }
}