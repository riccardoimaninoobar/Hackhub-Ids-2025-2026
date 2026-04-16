package it.unicam.hackhub.presentation;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.application.controller.*;
import it.unicam.hackhub.domain.model.HackathonBuilder;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.InvitoRepository;
import it.unicam.hackhub.domain.repository.TeamRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
// IMPORT AGGIUNTI PER RICHIESTA SUPPORTO
import it.unicam.hackhub.domain.repository.RichiestaSupportoRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryRichiestaSupportoRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryHackathonRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryInvitoRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryTeamRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryUtenteRepository;
import it.unicam.hackhub.presentation.cli.*;
import it.unicam.hackhub.application.controller.ProclamaVincitoreHandler;
import it.unicam.hackhub.presentation.cli.ProclamaVincitoreCLI;
import it.unicam.hackhub.application.controller.SistemaPagamentoAdapter;
import it.unicam.hackhub.infrastructure.DummySistemaPagamentoAdapter;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // ===============================
        // 1. Inizializzazione Sessione
        // ===============================
        Sessione sessioneApp = new Sessione(null);

        // ===============================
        // 2. Inizializzazione Repository
        // ===============================
        HackathonRepository hackathonRepo = new InMemoryHackathonRepository();
        UtenteRepository utenteRepo = new InMemoryUtenteRepository();
        TeamRepository teamRepo = new InMemoryTeamRepository();
        InvitoRepository invitoRepo = new InMemoryInvitoRepository();
        // NUOVO REPOSITORY
        RichiestaSupportoRepository richiestaRepo = new InMemoryRichiestaSupportoRepository();

        // ===============================
        // 3. Seed utenti iniziali
        // ===============================
        Utente o = new Utente("mock_organizzatore", "org@hack.it", "pass123");
        Utente g = new Utente("AnnaGiudice", "anna@hack.it", "pass123");
        Utente m = new Utente("LuigiMentore", "luigi@hack.it", "pass123");
        Utente mentore2 = new Utente("GianniManni", "gianbigman@hack.it", "pass123");
        Utente rizzler = new Utente("rizzler","therizzlerking@hack.it", "42069");

        utenteRepo.save(rizzler);
        utenteRepo.save(o);
        utenteRepo.save(g);
        utenteRepo.save(m);
        utenteRepo.save(mentore2);

        // ===============================
        // 4. Seed hackathon
        // ===============================
        HackathonBuilder hBuilder = new HackathonBuilder()
                .assegnaNome("hackProva")
                .assegnaRegolamento("non uccidere")
                .assegnaScadenza(LocalDate.parse("2026-01-01"))
                .assegnaDataInizio(LocalDate.parse("2026-01-10"))
                .assegnaDataFine(LocalDate.parse("2026-03-01"))
                .assegnaLuogo("Camerino")
                .assegnaDimMaxTeam(5)
                .assegnaPremioImporto(new BigDecimal(1000))
                .assegnaOrganizzatore(o)
                .assegnaGiudice(g)
                .assegnaMentore(m);
        hackathonRepo.save(hBuilder.build());

        // ===============================
        // 5. Inizializzazione Handler
        // ===============================
        LoginHandler loginHandler = new LoginHandler(utenteRepo, sessioneApp);
        RegistrazioneHandler registrazioneHandler = new RegistrazioneHandler(utenteRepo, sessioneApp);
        CreazioneTeamHandler teamHandler = new CreazioneTeamHandler(teamRepo, sessioneApp);
        AggiungiMentoreHandler aggMentoreHandler = new AggiungiMentoreHandler(hackathonRepo, utenteRepo, sessioneApp);
        CreazioneHackathonHandler hackathonHandler = new CreazioneHackathonHandler(hackathonRepo, utenteRepo, aggMentoreHandler, sessioneApp);
        ConsultareHackathonHandler consultareHackathonHandler = new ConsultareHackathonHandler(hackathonRepo);
        IscrizioneTeamHandler iscrizioneTeamHandler = new IscrizioneTeamHandler(hackathonRepo, teamRepo, sessioneApp);
        CaricaSottomissioneHandler caricaSottomissioneHandler = new CaricaSottomissioneHandler(hackathonRepo,  sessioneApp);
        GestioneInvitiHandler invitiHandler = new GestioneInvitiHandler(utenteRepo, invitoRepo, sessioneApp);
        AccettazioneInvitoHandler accettazioneInvitoHandler = new AccettazioneInvitoHandler(invitoRepo, sessioneApp);
        RichiestaSupportoHandler richiestaSupportoHandler = new RichiestaSupportoHandler(sessioneApp, richiestaRepo);
        LogoutHandler logoutHandler = new LogoutHandler(sessioneApp);

        // ===============================
        // 6. Inizializzazione CLI
        // ===============================
        LoginCLI loginCli = new LoginCLI(loginHandler);
        RegistrazioneCLI regCli = new RegistrazioneCLI(registrazioneHandler);
        ConsultareHackathonCLI consultareHackathonCLI = new ConsultareHackathonCLI(consultareHackathonHandler);
        CreazioneTeamCLI teamCli = new CreazioneTeamCLI(teamHandler, sessioneApp);
        CreazioneHackathonCLI hackathonCli = new CreazioneHackathonCLI(hackathonHandler);
        AggiungiMentoreCLI aggMentoreCli = new AggiungiMentoreCLI(aggMentoreHandler, sessioneApp);
        IscrizioneTeamCLI iscrizioneTeamCLI = new IscrizioneTeamCLI(iscrizioneTeamHandler, sessioneApp);
        CaricaSottomissioneCLI caricaSottomissioneCLI = new CaricaSottomissioneCLI(caricaSottomissioneHandler);
        GestioneInvitiCLI invitiCli = new GestioneInvitiCLI(invitiHandler);
        AccettazioneInvitoCLI accettazioneInvitoCLI = new AccettazioneInvitoCLI(accettazioneInvitoHandler);
        RichiestaSupportoCLI richiestaSupportoCLI = new RichiestaSupportoCLI(richiestaSupportoHandler);
        LogoutCLI logoutCLI = new LogoutCLI(logoutHandler);
        
        SistemaPagamentoAdapter sistemaPagamentoAdapter = new DummySistemaPagamentoAdapter();
        ProclamaVincitoreHandler proclamaVincitoreHandler = new ProclamaVincitoreHandler(hackathonRepo, teamRepo, sistemaPagamentoAdapter);
        ProclamaVincitoreCLI proclamaVincitoreCLI = new ProclamaVincitoreCLI(proclamaVincitoreHandler, sessioneApp);

        // ===============================
        // Menu applicazione
        // ===============================
        Scanner scanner = new Scanner(System.in);
        boolean appInEsecuzione = true;

        System.out.println("=====================================================");
        System.out.println("                 BENVENUTO IN HACKHUB                ");
        System.out.println("=====================================================");

        while (appInEsecuzione) {
            System.out.println("\n-----------------------------------------------------");
            Utente utenteCorrente = sessioneApp.getUtenteCorrente();
            if (utenteCorrente != null) {
                System.out.println("UTENTE LOGGATO: " + utenteCorrente.getUsername());
            } else {
                System.out.println("NESSUN UTENTE LOGGATO");
            }
            System.out.println("-----------------------------------------------------");

            System.out.println("HackHub, cosa vuoi fare?");
            System.out.println("1 - Creare Team");
            System.out.println("2 - Creare Hackathon");
            System.out.println("3 - Effettuare registrazione");
            System.out.println("4 - Aggiungere Mentore");
            System.out.println("5 - Effettuare login");
            System.out.println("6 - Consultare Hackathon");
            System.out.println("7 - Iscrivere Team ad Hackathon");
            System.out.println("8 - Caricare Sottomissione");
            System.out.println("9 - Invitare a entrare nel team");
            System.out.println("10 - Accettare invito nel team");
            System.out.println("11 - Inviare richiesta di supporto");
            System.out.println("12 - Effettuare logout");
            System.out.println("13 - Proclama Team Vincitore (Include Erogazione Premio)");
            System.out.println("0 - Uscire dall'applicazione");
            System.out.print("Scelta: ");

            String scelta = scanner.nextLine();

            try {
                switch (scelta) {
                    case "1": teamCli.run(); break;
                    case "2": hackathonCli.run(); break;
                    case "3": regCli.run(); break;
                    case "4": aggMentoreCli.run(); break;
                    case "5": loginCli.run(); break;
                    case "6": consultareHackathonCLI.consultaHackathon(); break;
                    case "7": iscrizioneTeamCLI.richiediIscrizioneTeam(); break;
                    case "8": caricaSottomissioneCLI.richiediCaricamentoSottomissione(); break;
                    case "9": invitiCli.run(); break;
                    case "10": accettazioneInvitoCLI.avviaGestioneInviti(); break;
                    case "11": richiestaSupportoCLI.avviaRichiestaSupporto(); break;
                    case "12": logoutCLI.richiediLogout(); break;
                    case "13": proclamaVincitoreCLI.avviaMenu(); break;
                    case "0":
                        System.out.println("\nChiusura di HackHub in corso... Arrivederci!");
                        appInEsecuzione = false;
                        break;
                    default:
                        System.out.println("\nScelta non valida.");
                        break;
                }
            } catch (Exception e) {
                System.err.println("Si è verificato un errore inaspettato: " + e.getMessage());
            }
        }
        scanner.close();
    }
}