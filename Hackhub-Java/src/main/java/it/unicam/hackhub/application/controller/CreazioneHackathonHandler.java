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

    /**
     * Passo 1: Inizio creazione e assegnazione Organizzatore
     */
    public void iniziaCreazione(String nome, String nomeOrganizzatore) {
        // Recuperiamo l'Utente dalla repository tramite ID (email)
        Utente u = utenteRepo.findById(nomeOrganizzatore)
                .orElseThrow(() -> new IllegalArgumentException("Utente organizzatore non trovato."));

        // Creiamo il ruolo specifico (Associazione MembroStaff -> Utente)
        Organizzatore org = new Organizzatore(u);

        // Inizializziamo il builder
        this.currentBuilder = new HackathonBuilder()
                .assegnaNome(nome)
                .assegnaOrganizzatore(org);
    }

    /**
     * Passo 2: Definizione delle date del caso d'uso
     */
    public void impostaDate(LocalDate inizio, LocalDate fine, LocalDate scadenza) {
        checkBuilder();
        currentBuilder.assegnaDataInizio(inizio)
                .assegnaDataFine(fine)
                .assegnaScadenza(scadenza);
    }

    /**
     * Passo 3: Dettagli logistici e premio
     */
    public void impostaDettagli(String luogo, Integer maxTeam, BigDecimal premio) {
        checkBuilder();
        currentBuilder.assegnaLuogo(luogo)
                .assegnaDimMaxTeam(maxTeam)
                .assegnaPremioImporto(premio);
    }

    /**
     * Passo Finale: Conferma e salvataggio
     */
    public void confermaCreazione() {
        checkBuilder();

        // Il builder genera l'entità Hackathon
        Hackathon nuovoHackathon = currentBuilder.build();

        // La repository specifica lo salva in memoria
        hackathonRepo.save(nuovoHackathon);

        // Reset della sessione di creazione
        this.currentBuilder = null;
    }

    private void checkBuilder() {
        if (currentBuilder == null) {
            throw new IllegalStateException("Nessuna creazione in corso. Chiama prima iniziaCreazione().");
        }
    }
}