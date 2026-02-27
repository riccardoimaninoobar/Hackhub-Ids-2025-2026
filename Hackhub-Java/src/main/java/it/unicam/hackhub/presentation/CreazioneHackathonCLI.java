package it.unicam.hackhub.presentation;

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

    public void run(Utente currentUtente) {
        System.out.println("[1] L'Organizzatore richiede di creare un Hackathon...");

        String nome, regolamento, luogo;
        LocalDate scadenzaIscrizioni, dataInizio, dataFine;
        Integer dimMaxTeam;
        BigDecimal premio;

        while (true) {
            // STEP 2: Richiesta ESPLICITA di tutti i campi previsti dal diagramma
            System.out.println("\n[2] Inserisci i dati per il nuovo Hackathon:");
            
            System.out.print(" -> Nome Hackathon: ");
            nome = scanner.nextLine();
            
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

            // STEP 4: Controllo che il nome non esista
            System.out.println("\n[4] SYSTEM verifica l'unicità del nome...");
            if (handler.hackathonExists(nome)) {
                System.out.println("[4.a.1] ❌ Errore: Un Hackathon con questo nome esiste già!");
                System.out.println("[4.a.2] Il flusso riparte dal punto 2 (reinserimento dati).");
                continue; 
            }
            break; // Se non esiste, il ciclo si rompe e si va avanti
        }

        // STEP 5: Creazione dell'oggetto e associazione organizzatore
        System.out.println("[5] SYSTEM crea l'Hackathon in memoria e vi associa l'Organizzatore.");
        handler.creaHackathonBase(currentUtente, nome, regolamento, scadenzaIscrizioni, dataInizio, dataFine, luogo, dimMaxTeam, premio);

        // STEP 6 & 7: Richiesta e Inserimento Giudice
        System.out.print("\n[6] SYSTEM richiede un Giudice.\n[7] Inserisci l'ID del Giudice (es. AnnaGiudice): ");
        String idGiudice = scanner.nextLine();
        
        // STEP 8
        System.out.println("[8] SYSTEM associa il Giudice all'Hackathon.");
        handler.assegnaGiudice(idGiudice);

        // STEP 9 & 10: Richiesta e Inserimento Mentori (con obbligo di almeno 1)
        System.out.println("\n[9] SYSTEM richiede di assegnare uno o più Mentori.");
        int mentoriAggiunti = 0;
        
        while (true) {
            System.out.print("[10] Inserisci ID del Mentore (premi Invio a vuoto per terminare la lista): ");
            String idMentore = scanner.nextLine();
            
            if (idMentore.trim().isEmpty()) {
                if (mentoriAggiunti >= 1) {
                    break; // Condizione di "almeno uno" rispettata
                } else {
                    System.out.println(" ❌ Errore: Devi inserire ALMENO UN Mentore prima di proseguire!");
                    continue;
                }
            }
            
            // STEP 11
            System.out.println("[11] SYSTEM associa il Mentore '" + idMentore + "' all'Hackathon.");
            handler.assegnaMentore(idMentore);
            mentoriAggiunti++;
        }

        // STEP 12
        handler.confermaCreazione();
        System.out.println("\n[12] ✅ SYSTEM salva i dati. Il caso d'uso termina con successo!");
    }
}