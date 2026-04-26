package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.*;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class CreazioneHackathonHandler {

    private final HackathonRepository hackathonRepo;
    private final UtenteRepository utenteRepo;
    private HackathonBuilder currentBuilder;
    private AggiungiMentoreHandler aggiungiMentoreHandler;
    private Hackathon hackathon;

    private final Sessione sessione; // AGGIUNTA

    public CreazioneHackathonHandler(HackathonRepository hRepo,
                                     UtenteRepository uRepo,
                                     AggiungiMentoreHandler aggMentoreHandler,
                                     Sessione sessione) {
        this.hackathonRepo = hRepo;
        this.utenteRepo = uRepo;
        this.aggiungiMentoreHandler = aggMentoreHandler;
        this.sessione = sessione;
    }

    public void checkPrerequisiti() {
        if (sessione.getUtenteCorrente() == null) {
            throw new IllegalStateException("Devi effettuare il login per poter creare un Hackathon.");
        }
    }

    public boolean hackathonExists(String nome) {
        return hackathonRepo.findByNome(nome).isPresent();
    }

    // RIMOSSO: il parametro "Utente organizzatore" non è più passato dalla CLI
    public void creaHackathonBase(String nome, String regolamento,
                                  LocalDate scadenza, LocalDate inizio, LocalDate fine,
                                  String luogo, Integer maxTeam, BigDecimal premio) {

        // L'organizzatore è colui che sta effettuando l'operazione in sessione
        Utente organizzatore = sessione.getUtenteCorrente();
        if (organizzatore == null) {
            throw new IllegalStateException("Devi effettuare il login per creare un Hackathon.");
        }

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

    public boolean assegnaGiudice(String nomeGiudice) {
        checkBuilder();
        var optUtente = utenteRepo.findByUsername(nomeGiudice);
        if(optUtente.isEmpty()) {
            return false;
        }
        Utente u = optUtente.get();
        currentBuilder.assegnaGiudice(u);
        this.hackathon = currentBuilder.build();
        hackathonRepo.save(hackathon);
        this.currentBuilder = null;
        return true;
    }

    public void assegnaMentore(String idMentore) {
        aggiungiMentoreHandler.checkOrg(hackathon.getNome());
        aggiungiMentoreHandler.aggiungiMentore(idMentore);
    }

    private void checkBuilder() {
        if (currentBuilder == null) {
            throw new IllegalStateException("Nessuna creazione in corso.");
        }
    }
}