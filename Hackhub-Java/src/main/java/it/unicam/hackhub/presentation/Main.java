package it.unicam.hackhub.presentation;

import it.unicam.hackhub.domain.model.User;
import it.unicam.hackhub.presentation.controllers.CreazioneTeamCLI;
import it.unicam.hackhub.service.CreazioneTeamHandler;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Inizializziamo l'Handler che manterrà in memoria i team creati durante la sessione
        CreazioneTeamHandler teamHandler = new CreazioneTeamHandler();

        // Simuliamo un utente che ha appena fatto il login nell'app
        User currentUser = new User("MarioRossi", "mario@hack.it");

        Scanner scanner = new Scanner(System.in);
        boolean appInEsecuzione = true;

        System.out.println("=====================================================");
        System.out.println("                 BENVENUTO IN HACKHUB                ");
        System.out.println("=====================================================");
        System.out.println("👤 Utente loggato: " + currentUser.getUsername());

        // Menu Interattivo dell'applicazione
        while (appInEsecuzione) {
            System.out.println("\n-----------------------------------------------------");
            System.out.println("HackHub, cosa vuoi fare?");
            System.out.println("Premi 1 per andare al caso d'uso: Crea Team");
            System.out.println("Premi 0 per uscire dall'applicazione");
            System.out.print("👉 Scelta: ");

            String scelta = scanner.nextLine();

            switch (scelta) {
                case "1":
                    System.out.println("\n>>> AVVIO FLUSSO: CREA TEAM <<<");
                    // Avviamo la CLI specifica per la creazione del team
                    CreazioneTeamCLI teamCli = new CreazioneTeamCLI(teamHandler);
                    teamCli.createTeam(currentUser);
                    break;

                case "0":
                    System.out.println("\nChiusura di HackHub in corso... Arrivederci!");
                    appInEsecuzione = false; // Ferma il ciclo e chiude l'app
                    break;

                default:
                    System.out.println("\n❌ Scelta non valida. Per favore, premi 1 o 0.");
                    break;
            }
        }

        scanner.close();
    }
}