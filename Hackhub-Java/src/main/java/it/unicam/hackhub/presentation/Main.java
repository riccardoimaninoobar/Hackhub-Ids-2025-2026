package it.unicam.hackhub.presentation;

import it.unicam.hackhub.application.controller.CreazioneHackathonHandler;
import it.unicam.hackhub.application.controller.CreazioneTeamHandler;
import it.unicam.hackhub.application.controller.RegistrazioneHandler;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.TeamRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryHackathonRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryTeamRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryUtenteRepository;
import it.unicam.hackhub.presentation.cli.CreazioneHackathonCLI;
import it.unicam.hackhub.presentation.cli.CreazioneTeamCLI;
import it.unicam.hackhub.presentation.cli.RegistrazioneCLI;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // ===============================
        // Inizializzazione dipendenze
        // ===============================

        // UC: Crea Team (in memoria, solo runtime)
        TeamRepository teamRepo = new InMemoryTeamRepository();
        CreazioneTeamHandler teamHandler = new CreazioneTeamHandler(teamRepo);
        CreazioneTeamCLI teamCli = new CreazioneTeamCLI(teamHandler);

        // UC: Crea Hackathon (repository in memoria)
        HackathonRepository hackathonRepo = new InMemoryHackathonRepository();
        UtenteRepository utenteRepo = new InMemoryUtenteRepository();
        CreazioneHackathonHandler hackathonHandler = new CreazioneHackathonHandler(hackathonRepo, utenteRepo);
        CreazioneHackathonCLI hackathonCli = new CreazioneHackathonCLI(hackathonHandler);

        // UC: Registrazione Visitatore
        RegistrazioneHandler registrazioneHandler = new RegistrazioneHandler(utenteRepo);

        // ===============================
        // Seed utenti (per UC Crea Hackathon)
        // ===============================

        // Utente loggato (simulazione login)
        Utente currentUtente = new Utente("MarioRossi", "mario@hack.it", "a1234");
        /* 
        Team teamProva = new Team("teamProva");
        teamProva.addMember(currentUtente);
        currentUtente.setTeam(teamProva);
        */
        // Utenti disponibili per assegnazione (giudice/mentore)
        Utente giudice = new Utente("AnnaGiudice", "anna@hack.it", "pass123");
        Utente mentore = new Utente("LuigiMentore", "luigi@hack.it", "pass123");

        // Salvo nel repository utenti così la CLI dell'Hackathon può trovarli per ID
        utenteRepo.save(currentUtente);
        utenteRepo.save(giudice);
        utenteRepo.save(mentore);

        // ===============================
        // Menu applicazione
        // ===============================

        Scanner scanner = new Scanner(System.in);
        boolean appInEsecuzione = true;

        System.out.println("=====================================================");
        System.out.println("                 BENVENUTO IN HACKHUB                ");
        System.out.println("=====================================================");
        System.out.println(" Utente loggato: " + currentUtente.getUsername());
        System.out.println("\nPromemoria ID Utenti registrati nel sistema:");
        System.out.println("- Giudice da poter assegnare: AnnaGiudice");
        System.out.println("- Mentore da poter assegnare: LuigiMentore");

        while (appInEsecuzione) {
            System.out.println("\n-----------------------------------------------------");
            System.out.println("HackHub, cosa vuoi fare?");
            System.out.println("Premi 1 per andare al caso d'uso: Crea Team");
            System.out.println("Premi 2 per andare al caso d'uso: Crea Hackathon");
            System.out.println("Premi 3 per andare al caso d'uso: Registrazione Visitatore");
            System.out.println("Premi 0 per uscire dall'applicazione");
            System.out.print(" Scelta: ");

            String scelta = scanner.nextLine();

            switch (scelta) {
                case "1":
                    System.out.println("\n>>> AVVIO FLUSSO: CREA TEAM <<<");
                    try {
                        teamCli.run(currentUtente);
                    } catch (Exception e) {
                        System.err.println("\nErrore durante l'esecuzione del caso d'uso Crea Team: " + e.getMessage());
                    }
                    break;

                case "2":
                    System.out.println("\n>>> AVVIO FLUSSO: CREA HACKATHON <<<");
                    try {
                        hackathonCli.run(currentUtente);
                    } catch (Exception e) {
                        System.err.println("\nErrore durante l'esecuzione del caso d'uso Crea Hackathon: " + e.getMessage());
                    }
                    break;

                case "3":
                    System.out.println("\n>>> AVVIO FLUSSO: REGISTRAZIONE <<<");
                    try {
                        RegistrazioneCLI regCli = new RegistrazioneCLI(registrazioneHandler);
                        Utente utenteAppenaRegistrato = regCli.run();
                        currentUtente = utenteAppenaRegistrato;
                    } catch (Exception e) {
                        System.err.println("\nErrore durante l'esecuzione del caso d'uso Registrazione: " + e.getMessage());
                    }
                    break;

                case "0":
                    System.out.println("\nChiusura di HackHub in corso... Arrivederci!");
                    appInEsecuzione = false;
                    break;

                default:
                    System.out.println("\n Scelta non valida. Per favore, premi 1, 2 o 0.");
                    break;
            }
        }

        scanner.close();
    }
}