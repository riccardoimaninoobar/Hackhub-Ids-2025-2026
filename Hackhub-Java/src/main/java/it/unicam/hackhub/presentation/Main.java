package it.unicam.hackhub.presentation;

import it.unicam.hackhub.application.controller.CreazioneHackathonHandler;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryHackathonRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryUtenteRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        // 1. INIZIALIZZAZIONE INFRASTRUTTURA
        // Creiamo le istanze concrete delle repository
        HackathonRepository hackathonRepo = new InMemoryHackathonRepository();
        UtenteRepository utenteRepo = new InMemoryUtenteRepository();

        // 2. SETUP DATI INIZIALI (Simuliamo utenti registrati)
        Utente orgUtente = new Utente("MarioRossi", "mario@hack.it", "password123");
        utenteRepo.save(orgUtente);

        // 3. INIZIALIZZAZIONE HANDLER
        CreazioneHackathonHandler handler = new CreazioneHackathonHandler(hackathonRepo, utenteRepo);

        try {
            System.out.println("--- Inizio Caso d'Uso: Creazione Hackathon ---");

            // Fase 1: Inizio (System Event)
            handler.iniziaCreazione("Generative AI Challenge", "MarioRossi");
            System.out.println("1. Hackathon iniziato e Organizzatore assegnato correttamente.");

            // Fase 2: Impostazione Date
            handler.impostaDate(
                    LocalDate.of(2025, 6, 1),  // Inizio
                    LocalDate.of(2025, 6, 3),  // Fine
                    LocalDate.of(2025, 5, 20)  // Scadenza iscrizioni
            );
            System.out.println("2. Date impostate.");

            // Fase 3: Dettagli logistici
            handler.impostaDettagli("Polo Informatico UNICAM", 10, new BigDecimal("1500.00"));
            System.out.println("3. Dettagli e premio impostati.");

            // Fase 4: Conferma finale
            handler.confermaCreazione();
            System.out.println("4. Conferma ricevuta! Hackathon salvato con successo.");

            // VERIFICA
            System.out.println("\n--- Verifica Repository ---");
            hackathonRepo.findAll().forEach(h -> {
                System.out.println("Hackathon in DB: " + h.getNome());
                System.out.println("Organizzatore: " + h.getOrganizzatore().getUtente().getNome());
            });

        } catch (Exception e) {
            System.err.println("Errore durante la creazione: " + e.getMessage());
        }
    }
}