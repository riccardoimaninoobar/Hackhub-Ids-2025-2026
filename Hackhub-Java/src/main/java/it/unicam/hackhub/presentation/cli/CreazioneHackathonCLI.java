package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.application.controller.CreazioneHackathonHandler;
import it.unicam.hackhub.domain.model.Utente;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

public class CreazioneHackathonCLI {
    private final CreazioneHackathonHandler handler;
    private final Scanner scanner = new Scanner(System.in);

    public CreazioneHackathonCLI(CreazioneHackathonHandler handler) {
        this.handler = handler;
    }
    public void inserisciGiudice(){
        System.out.println("SYSTEM associa il Giudice all'Hackathon.");
        boolean ok;
        String idGiudice = "";
        do {
            ok = handler.assegnaGiudice(idGiudice);
                
            if (!ok) {
                System.out.println("Utente non trovato, inserire id utente: ");
                idGiudice = scanner.nextLine();
            }

        } while (!ok);
    }

    public void inserisciMentore(){System.out.println("\nSYSTEM richiede di assegnare uno o più Mentori.");
        int mentoriAggiunti = 0;
        boolean ok;
        while (true) {
            System.out.print("Inserisci ID del Mentore (premi Invio a vuoto per terminare la lista): ");
            String idMentore = scanner.nextLine();
            
            if (idMentore.trim().isEmpty()) {
                if (mentoriAggiunti >= 1) {
                    break; // Condizione di "almeno uno" rispettata
                } else {
                    System.out.println("Errore: Devi inserire ALMENO un Mentore prima di proseguire!");
                    continue;
                }
            }
            
            // STEP 11
            System.out.println("SYSTEM associa il Mentore '" + idMentore + "' all'Hackathon.");
            ok = handler.assegnaMentore(idMentore);
            if (ok) {
                mentoriAggiunti++;
            } else System.out.println("Utente non trovato, inserire username corretto");
        }
    }

    public void run(Utente currentUtente) {
        System.out.println("L'Organizzatore richiede di creare un Hackathon...");

        String nome, regolamento, luogo;
        LocalDate scadenzaIscrizioni, dataInizio, dataFine;
        Integer dimMaxTeam;
        BigDecimal premio;

        while (true) {
            // STEP 2: Richiesta ESPLICITA di tutti i campi previsti dal diagramma
            System.out.println("\nInserisci i dati per il nuovo Hackathon:");
            
            System.out.print(" -> Nome Hackathon: ");
            nome = scanner.nextLine();
            // STEP 3: Controllo che il nome non esista
            System.out.println("\nSYSTEM verifica l'unicità del nome...");
            if (handler.hackathonExists(nome)) {
                System.out.println("Errore: Un Hackathon con questo nome esiste già!");
                System.out.println("Il flusso riparte dal punto 2 (reinserimento dati).");
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

            break; // Se non esiste, il ciclo si rompe e si va avanti
        }

        // STEP 4: Creazione dell'oggetto e associazione organizzatore
        System.out.println("SYSTEM crea l'Organizzatore e inizializza Hackathon builder");
        handler.creaHackathonBase(currentUtente, nome, regolamento, scadenzaIscrizioni, dataInizio, dataFine, luogo, dimMaxTeam, premio);

        // STEP 6 & 7: Richiesta e Inserimento Giudice
        System.out.print("\nSYSTEM richiede un Giudice.\n[7] Inserisci l'ID del Giudice (es. AnnaGiudice): ");
        String idGiudice = scanner.nextLine();
        
        // STEP 8
        this.inserisciGiudice();

        // STEP 9 & 10: Richiesta e Inserimento Mentori (con obbligo di almeno 1)
        this.inserisciMentore();
        
        // STEP 11

        // STEP 12
        handler.confermaCreazione();
        System.out.println("\nSYSTEM salva i dati. Il caso d'uso termina con successo!");
    }
}