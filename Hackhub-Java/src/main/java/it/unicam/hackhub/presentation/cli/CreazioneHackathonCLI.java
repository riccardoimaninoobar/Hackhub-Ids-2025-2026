package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.application.controller.CreazioneHackathonHandler;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

@Component
public class CreazioneHackathonCLI {
    private final CreazioneHackathonHandler handler;
    private final Scanner scanner = new Scanner(System.in);

    public CreazioneHackathonCLI(CreazioneHackathonHandler handler) {
        this.handler = handler;
    }

    // Il metodo run() simula le azioni umane (l'Organizzatore che legge lo schermo e digita)
    public void run() {
        try {
            // 1. L'Organizzatore richiede di iniziare
            richiediInserimentoHackathon();
        } catch (IllegalStateException e) {
            System.out.println("Errore: " + e.getMessage());
            return; // Early Exit (Fragment break)
        }

        System.out.println("\n>>> CREA HACKATHON <<<");
        String nome, regolamento, luogo;
        LocalDate scadenzaIscrizioni, dataInizio, dataFine;
        Integer dimMaxTeam;
        BigDecimal premio;

        while (true) {
            System.out.print(" -> Nome Hackathon: ");
            nome = scanner.nextLine();

            // 2. L'Organizzatore inserisce il nome
            boolean esiste = inserisciNomeHackathon(nome);
            if (esiste) {
                System.out.println("Errore: Un Hackathon con questo nome esiste già!");
                continue;
            }

            System.out.print(" -> Regolamento: ");
            regolamento = scanner.nextLine();
            System.out.print(" -> Data scadenza iscrizioni (es. 2025-05-01): ");
            scadenzaIscrizioni = LocalDate.parse(scanner.nextLine());
            System.out.print(" -> Data inizio (es. 2025-06-01): ");
            dataInizio = LocalDate.parse(scanner.nextLine());
            System.out.print(" -> Data fine (es. 2025-06-03): ");
            dataFine = LocalDate.parse(scanner.nextLine());
            System.out.print(" -> Luogo: ");
            luogo = scanner.nextLine();
            System.out.print(" -> Dimensione massima del team (es. 5): ");
            dimMaxTeam = Integer.valueOf(scanner.nextLine());
            System.out.print(" -> Premio in denaro (es. 1500.00): ");
            premio = new BigDecimal(scanner.nextLine());
            break;
        }

        // 3. L'Organizzatore inserisce il resto dei dati
        inserisciAltriDati(nome, regolamento, scadenzaIscrizioni, dataInizio, dataFine, luogo, dimMaxTeam, premio);

        boolean okGiudice = false;
        while (!okGiudice) {
            System.out.print("\nInserisci l'ID del Giudice (es. AnnaGiudice): ");
            String idGiudice = scanner.nextLine();

            // 4. L'Organizzatore inserisce il giudice
            okGiudice = inserisciGiudice(idGiudice);
            if (!okGiudice) System.out.println("Giudice non trovato, riprova.");
        }

        // 5. Blocco "Aggiungere Mentore Sequence Diagram"
        boolean okMentore = false;
        while (!okMentore) {
            System.out.print("\nInserisci l'ID di un Mentore (es. LuigiMentore): ");
            try {
                inserisciMentore(scanner.nextLine());
                okMentore = true;
            } catch (Exception e) {
                System.out.println("Errore Mentore: " + e.getMessage());
            }
        }
        System.out.println("\nHackathon creato e salvato con successo!");
    }

    // =====================================================================
    // METODI MAPPATI ESATTAMENTE SUL SEQUENCE DIAGRAM (Frecce in entrata)
    // =====================================================================

    public void richiediInserimentoHackathon() {
        handler.checkPrerequisiti();
    }

    public boolean inserisciNomeHackathon(String nome) {
        // Corrisponde alla freccia "hackathonPresente(nome)" verso l'Handler
        return handler.hackathonExists(nome);
    }

    public void inserisciAltriDati(String nome, String regolamento, LocalDate scadenzaIscrizioni,
                                   LocalDate dataInizio, LocalDate dataFine, String luogo,
                                   Integer dimMaxTeam, BigDecimal premio) {
        // Corrisponde alla freccia "creaHackathon(dati)" verso l'Handler
        handler.creaHackathonBase(nome, regolamento, scadenzaIscrizioni, dataInizio, dataFine, luogo, dimMaxTeam, premio);
    }

    public boolean inserisciGiudice(String username) {
        // Corrisponde alla freccia "assegnaGiudice(username)" verso l'Handler
        return handler.assegnaGiudice(username);
    }

    // Per gestire il Fragment "Ref" dell'aggiunta mentore
    public void inserisciMentore(String username) {
        handler.assegnaMentore(username);
    }
}