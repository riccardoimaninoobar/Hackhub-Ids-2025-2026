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
        return hackathonRepo.findAll().stream()
                .anyMatch(h -> h.getNome().equalsIgnoreCase(nome));
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
    public void assegnaGiudice(String idGiudice) {
        checkBuilder();
        Utente u = utenteRepo.findById(idGiudice)
                .orElseThrow(() -> new IllegalArgumentException("Utente giudice non trovato."));
        currentBuilder.assegnaGiudice(new Giudice(u));
    }

    // --- STEP 11: Associa i Mentori ---
    public void assegnaMentore(String idMentore) {
        checkBuilder();
        Utente u = utenteRepo.findById(idMentore)
                .orElseThrow(() -> new IllegalArgumentException("Utente mentore non trovato."));
        currentBuilder.assegnaMentore(new Mentore(u));
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