package it.unicam.hackhub.presentation;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.HackathonBuilder;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import it.unicam.hackhub.presentation.cli.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

@Component
public class CliRunner implements CommandLineRunner {

    private final Sessione sessioneApp;
    private final HackathonRepository hackathonRepo;
    private final UtenteRepository utenteRepo;

    // Tutte le CLI iniettate automaticamente da Spring
    private final CreazioneTeamCLI teamCli;
    private final CreazioneHackathonCLI hackathonCli;
    private final RegistrazioneCLI regCli;
    private final AggiungiMentoreCLI aggMentoreCli;
    private final LoginCLI loginCli;
    private final ConsultareHackathonCLI consultareHackathonCLI;
    private final IscrizioneTeamCLI iscrizioneTeamCLI;
    private final CaricaSottomissioneCLI caricaSottomissioneCLI;
    private final GestioneInvitiCLI invitiCli;
    private final AccettazioneInvitoCLI accettazioneInvitoCLI;
    private final RichiestaSupportoCLI richiestaSupportoCLI;
    private final GRichiestaSupportoCLI gRichiestaSupportoCLI;
    private final LogoutCLI logoutCLI;
    private final ProclamaVincitoreCLI proclamaVincitoreCLI;

    public CliRunner(Sessione sessioneApp, HackathonRepository hackathonRepo, UtenteRepository utenteRepo,
                     CreazioneTeamCLI teamCli, CreazioneHackathonCLI hackathonCli, RegistrazioneCLI regCli,
                     AggiungiMentoreCLI aggMentoreCli, LoginCLI loginCli, ConsultareHackathonCLI consultareHackathonCLI,
                     IscrizioneTeamCLI iscrizioneTeamCLI, CaricaSottomissioneCLI caricaSottomissioneCLI,
                     GestioneInvitiCLI invitiCli, AccettazioneInvitoCLI accettazioneInvitoCLI,
                     RichiestaSupportoCLI richiestaSupportoCLI, GRichiestaSupportoCLI gRichiestaSupportoCLI,
                     LogoutCLI logoutCLI, ProclamaVincitoreCLI proclamaVincitoreCLI) {
        this.sessioneApp = sessioneApp;
        this.hackathonRepo = hackathonRepo;
        this.utenteRepo = utenteRepo;
        this.teamCli = teamCli;
        this.hackathonCli = hackathonCli;
        this.regCli = regCli;
        this.aggMentoreCli = aggMentoreCli;
        this.loginCli = loginCli;
        this.consultareHackathonCLI = consultareHackathonCLI;
        this.iscrizioneTeamCLI = iscrizioneTeamCLI;
        this.caricaSottomissioneCLI = caricaSottomissioneCLI;
        this.invitiCli = invitiCli;
        this.accettazioneInvitoCLI = accettazioneInvitoCLI;
        this.richiestaSupportoCLI = richiestaSupportoCLI;
        this.gRichiestaSupportoCLI = gRichiestaSupportoCLI;
        this.logoutCLI = logoutCLI;
        this.proclamaVincitoreCLI = proclamaVincitoreCLI;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Dati di Seed Iniziali (Salvati nei repository gestiti da Spring)
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

        HackathonBuilder hBuilder = new HackathonBuilder()
                .assegnaNome("hackProva")
                .assegnaRegolamento("non uccidere")
                .assegnaScadenza(LocalDate.parse("2026-04-20"))
                .assegnaDataInizio(LocalDate.parse("2026-01-10"))
                .assegnaDataFine(LocalDate.parse("2026-03-01"))
                .assegnaLuogo("Camerino")
                .assegnaDimMaxTeam(5)
                .assegnaPremioImporto(new BigDecimal(1000))
                .assegnaOrganizzatore(o)
                .assegnaGiudice(g)
                .assegnaMentore(m);
        hackathonRepo.save(hBuilder.build());

        // 2. Avvio Menu Interattivo
        Scanner scanner = new Scanner(System.in);
        boolean appInEsecuzione = true;

        System.out.println("=====================================================");
        System.out.println("                 BENVENUTO IN HACKHUB                ");
        System.out.println("                (Spring Boot Edition)                ");
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
            System.out.println("12 - Gestire richieste di supporto");
            System.out.println("13 - Effettuare logout");
            System.out.println("14 - Proclama Team Vincitore (Include Erogazione Premio)");
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
                    case "12": gRichiestaSupportoCLI.gestisciRichieste(); break;
                    case "13": logoutCLI.richiediLogout(); break;
                    case "14": proclamaVincitoreCLI.avviaMenu(); break;
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
        // Quando il ciclo termina, il metodo finisce e Spring Boot si spegne dolcemente.
    }
}