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
    private AggiungiMentoreHandler aggiungiMentoreHandler;
    private Hackathon hackathon;
    public CreazioneHackathonHandler(HackathonRepository hRepo,
                                     UtenteRepository uRepo,
                                     AggiungiMentoreHandler aggMentoreHandler) {
        this.hackathonRepo = hRepo;
        this.utenteRepo = uRepo;
        this.aggiungiMentoreHandler = aggMentoreHandler;
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



        this.currentBuilder = new HackathonBuilder()
                .assegnaNome(nome)
                .assegnaRegolamento(regolamento)
                .assegnaScadenza(scadenza)
                .assegnaDataInizio(inizio)
                .assegnaDataFine(fine)
                .assegnaLuogo(luogo)
                .assegnaDimMaxTeam(maxTeam)
                .assegnaPremioImporto(premio)
                .assegnaOrganizzatore(organizzatore);
    }

    // --- STEP 8: Associa il Giudice ---
    public boolean assegnaGiudice(String idGiudice) {
        checkBuilder();
        var optUtente = utenteRepo.findById(idGiudice);
        if(optUtente.isEmpty()) {
            return false;
        }
        Utente u = optUtente.get();
        currentBuilder.assegnaGiudice(u);
        this.hackathon = currentBuilder.build();
        hackathonRepo.save(hackathon);
        this.currentBuilder = null; // Pulisce la memoria
        return true;
    }

    public void assegnaMentore(String idMentore) {
        aggiungiMentoreHandler.checkOrg(hackathon.getOrganizzatore(), hackathon.getNome());
        aggiungiMentoreHandler.aggiungiMentore(idMentore);
    }

    private void checkBuilder() {
        if (currentBuilder == null) {
            throw new IllegalStateException("Nessuna creazione in corso.");
        }
    }
}