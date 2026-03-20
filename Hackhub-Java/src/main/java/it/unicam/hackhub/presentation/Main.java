package it.unicam.hackhub.presentation;

import it.unicam.hackhub.application.controller.*;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.TeamRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryHackathonRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryTeamRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryUtenteRepository;
import it.unicam.hackhub.presentation.cli.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // ===============================
        // Inizializzazione dipendenze
        // ===============================
        // ===============================
        // Seed utenti (per UC Crea Hackathon)
        // ===============================

        // UC: Crea Hackathon (repository in memoria)
        HackathonRepository hackathonRepo = new InMemoryHackathonRepository();
        UtenteRepository utenteRepo = new InMemoryUtenteRepository();
        // Utente loggato (simulazione login)

        // UC: Consultare Hackathon
        ConsultareHackathonHandler consultareHackathonHandler = new ConsultareHackathonHandler(hackathonRepo);
        ConsultareHackathonCLI consultareHackathonCLI = new ConsultareHackathonCLI(consultareHackathonHandler);

        //UC: Carica Sottomissione
        CaricaSottomissioneHandler caricaSottomissioneHandler = new CaricaSottomissioneHandler(hackathonRepo);
        CaricaSottomissioneCLI caricaSottomissioneCLI = new CaricaSottomissioneCLI(caricaSottomissioneHandler);


        // Utenti disponibili per assegnazione (giudice/mentore)
        Utente giudice = new Utente("AnnaGiudice", "anna@hack.it", "pass123");
        Utente mentore = new Utente("LuigiMentore", "luigi@hack.it", "pass123");
        Utente mentore2 = new Utente("GianniManni", "gianbigman@hack.it", "pass123");
        Utente currentUtente = new Utente("rizzler","therizzlerking@hack.it", "42069");

        // Salvo nel repository utenti così la CLI dell'Hackathon può trovarli per ID
        utenteRepo.save(currentUtente);
        utenteRepo.save(giudice);
        utenteRepo.save(mentore);
        utenteRepo.save(mentore2);
        // UC: Crea Team (in memoria, solo runtime)
        TeamRepository teamRepo = new InMemoryTeamRepository();
        CreazioneTeamHandler teamHandler = new CreazioneTeamHandler(teamRepo);
        CreazioneTeamCLI teamCli = new CreazioneTeamCLI(teamHandler);

        IscrizioneTeamHandler iscrizioneTeamHandler = new IscrizioneTeamHandler(hackathonRepo, teamRepo);
        IscrizioneTeamCLI iscrizioneTeamCLI = new IscrizioneTeamCLI(iscrizioneTeamHandler);


        AggiungiMentoreHandler aggiungiMentoreHandler = new AggiungiMentoreHandler(hackathonRepo, utenteRepo);
        CreazioneHackathonHandler hackathonHandler = new CreazioneHackathonHandler(hackathonRepo, utenteRepo, aggiungiMentoreHandler);
        CreazioneHackathonCLI hackathonCli = new CreazioneHackathonCLI(hackathonHandler);

        // UC: Registrazione Visitatore
        RegistrazioneHandler registrazioneHandler = new RegistrazioneHandler(utenteRepo);

        // UC: Aggiungere Mentore
        AggiungiMentoreHandler aggMentoreHandler = new AggiungiMentoreHandler(hackathonRepo, utenteRepo);
        // ===============================
        // Menu applicazione
        // ===============================

        Scanner scanner = new Scanner(System.in);
        boolean appInEsecuzione = true;

        System.out.println("=====================================================");
        System.out.println("                 BENVENUTO IN HACKHUB                ");
        System.out.println("=====================================================");
//        System.out.println(" Utente loggato: " + currentUtente.getUsername());
        currentUtente = null;
        System.out.println("\nPromemoria ID Utenti registrati nel sistema:");
        System.out.println("- Giudice da poter assegnare: AnnaGiudice");
        System.out.println("- Mentore da poter assegnare: LuigiMentore");

        while (appInEsecuzione) {
            System.out.println("\n-----------------------------------------------------");
            System.out.println("HackHub, cosa vuoi fare?");
            System.out.println("Premi 1 per andare al caso d'uso: Creare Team");
            System.out.println("Premi 2 per andare al caso d'uso: Creare Hackathon");
            System.out.println("Premi 3 per andare al caso d'uso: Registrazione Visitatore");
            System.out.println("Premi 4 per andare al caso d'uso: Aggiungere Mentore");
            System.out.println("Premi 5 per andare al caso d'uso: Effettuare login");
            System.out.println("Premi 6 per andare al caso d'uso: Consultare Hackathon");
            System.out.println("Premi 7 per andare al caso d'uso: Iscrivere Team ad Hackathon");
            System.out.println("Premi 8 per andare al caso d'uso: Caricare Sottomissione");
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
                case "4":
                    System.out.println("\n>>> AVVIO FLUSSO: AGGIUNGERE MENTORE <<<");
                    try {
                        AggiungiMentoreCLI aggMentoreCli = new AggiungiMentoreCLI(aggMentoreHandler);
                        aggMentoreCli.run(currentUtente);

                    } catch (Exception e) {
                        System.err.println("Errore durante l'esecuzione del caso d'uso Aggiungere Mentore: " + e.getMessage());
                    }
                    break;
                case "5":
                    System.out.println("\n>>> AVVIO FLUSSO: EFFETTUARE LOGIN <<<");
                    try {
                        LoginHandler loginHandler = new LoginHandler(utenteRepo);
                        LoginCLI loginCli = new LoginCLI(loginHandler);
                        currentUtente = loginCli.run();
                    } catch (Exception e) {
                        System.err.println("Errore durante l'esecuzione del caso d'uso Effettuare Login: " + e.getMessage());
                    }
                    break;
                case "6":
                    System.out.println("\n>>> AVVIO FLUSSO: CONSULTARE HACKATHON <<<");
                    try {
                        consultareHackathonCLI.consultaHackathon();
                    } catch (Exception e) {
                        System.err.println(
                         "\nErrore durante l'esecuzione del caso d'uso Consultare Hackathon: "
                        + e.getMessage()
                         );
                    }
                    break;
                case "7":
                    System.out.println("\n>>> AVVIO FLUSSO: ISCRIVERE TEAM AD HACKATHON <<<");
                    try {
                         if (currentUtente == null || currentUtente.getTeam() == null) {
                            System.out.println("Devi essere autenticato e avere un team per iscriverti.");
                         } else {
                                    String teamId = currentUtente.getTeam().getName(); // TeamRepository usa il nome come ID
                                    iscrizioneTeamCLI.iscriviTeam(teamId);
                        }
                    } catch (Exception e) {
                        System.err.println("Errore durante l'esecuzione del caso d'uso Iscrivere Team ad Hackathon: "
                        + e.getMessage());
                    }
                    break;
                case "8":
                    System.out.println("\n>>> AVVIO FLUSSO: CARICARE SOTTOMISSIONE <<<");
                    try {
                        if (currentUtente == null || currentUtente.getTeam() == null) {
                            System.out.println("Devi essere autenticato e avere un team per caricare una sottomissione.");
                        } else {
                            caricaSottomissioneCLI.caricaSottomissione(currentUtente.getTeam());
                        }
                    } catch (Exception e) {
                        System.err.println("Errore durante l'esecuzione del caso d'uso Caricare Sottomissione: "
                        + e.getMessage());
                    }
                    break;
                case "0":
                    System.out.println("\nChiusura di HackHub in corso... Arrivederci!");
                    appInEsecuzione = false;
                    break;

                default:
                    System.out.println("\n Scelta non valida.");
                    break;
            }
        }

        scanner.close();
    }
}