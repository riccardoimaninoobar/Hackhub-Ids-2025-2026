package it.unicam.hackhub.presentation;

import it.unicam.hackhub.application.controller.CreazioneHackathonHandler;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryHackathonRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryUtenteRepository;

public class Main {
    public static void main(String[] args) {
        // 1. Inizializziamo il "database" in memoria
        HackathonRepository hackathonRepo = new InMemoryHackathonRepository();
        UtenteRepository utenteRepo = new InMemoryUtenteRepository();

        // 2. Creiamo degli utenti pre-registrati nel sistema. 
        // Servono perché quando al passo 7 e 10 la CLI ti chiederà "Inserisci Giudice/Mentore", 
        // tu dovrai digitare un ID che il sistema riconosce.
        Utente organizzatore = new Utente("MarioRossi", "mario@hack.it", "pass123");
        Utente giudice = new Utente("AnnaGiudice", "anna@hack.it", "pass123");
        Utente mentore = new Utente("LuigiMentore", "luigi@hack.it", "pass123");

        utenteRepo.save(organizzatore);
        utenteRepo.save(giudice);
        utenteRepo.save(mentore);

        System.out.println("=====================================================");
        System.out.println("   BENVENUTO IN HACKHUB - AMBIENTE DI TEST MANUALE   ");
        System.out.println("=====================================================");
        System.out.println("Promemoria ID Utenti registrati nel sistema:");
        System.out.println("- Giudice da poter assegnare: AnnaGiudice");
        System.out.println("- Mentore da poter assegnare: LuigiMentore");
        System.out.println("-----------------------------------------------------\n");

        // 3. Inizializziamo l'Handler e la CLI
        CreazioneHackathonHandler handler = new CreazioneHackathonHandler(hackathonRepo, utenteRepo);
        CreazioneHackathonCLI cli = new CreazioneHackathonCLI(handler);

        // 4. Avviamo l'inserimento manuale da tastiera! (Simuliamo l'accesso di MarioRossi)
        try {
            cli.run(organizzatore);
        } catch (Exception e) {
            System.err.println("\nErrore durante l'esecuzione: " + e.getMessage());
        }
    }
}