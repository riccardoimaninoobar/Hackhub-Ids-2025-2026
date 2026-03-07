package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.application.controller.RegistrazioneHandler;

import java.util.Scanner;

public class RegistrazioneCLI {

    private final RegistrazioneHandler handler;
    private final Scanner scanner;

    public RegistrazioneCLI(RegistrazioneHandler handler) {
        this.handler = handler;
        this.scanner = new Scanner(System.in);
    }

    public Utente run() {
        System.out.println("\n[1] Il Visitatore richiede di registrarsi al sistema...");

        String username, email, password;

        while (true) {
            // STEP 2: Inserimento dati
            System.out.println("\n[2] Inserisci i dati di registrazione:");
            System.out.print(" -> username: ");
            username = scanner.nextLine();
            
            while (true) {
                System.out.print(" -> E-mail: ");
                email = scanner.nextLine();
                try {
                    handler.validaEmail(email);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage() + " Riprova.");
                }
            }
            
            System.out.print(" -> Password: ");
            password = scanner.nextLine();

            // STEP 3: Validazione dei dati
            System.out.println("\n[3] SYSTEM valida i dati inseriti...");
            try {
                handler.validaDati(username, email, password);
            } catch (IllegalArgumentException e) {
                // STEP 3.a
                System.out.println("[3.a.1] SYSTEM segnala errore nella compilazione dei campi: " + e.getMessage());
                System.out.println("[3.a.2] Il Visitatore corregge la compilazione.");
                System.out.println("[3.a.3] Il flusso riprende dal punto 3.\n");
                continue; // Torna all'inizio del ciclo per reinserire i dati
            }

            // STEP 4: Verifica unicità
            System.out.println("[4] SYSTEM verifica che non esista già un utente con lo stesso username o e-mail...");
            if (handler.verificaUtenteEsistente(username, email)) {
                // STEP 4.a
                System.out.println("[4.a.1] SYSTEM segnala presenza utente con stesso username e/o e-mail.");
                System.out.println("[4.a.2] Il Visitatore modifica username e/o e-mail.");
                System.out.println("[4.a.3] Il flusso riprende dal punto 4.\n");
                continue; // Torna all'inizio del ciclo per riprovare
            }

            // Se supera entrambi i controlli, esce dal ciclo
            break;
        }

        // STEP 5 e 6: Creazione e fine
        System.out.println("\n[5] SYSTEM crea il nuovo utente.");
        Utente nuovoUtente = handler.registraUtente(username, email, password);
        System.out.println(" Registrazione completata con successo! Benvenuto " + nuovoUtente.getUsername() + ".");
        
        System.out.println("[6] Fine Caso d'Uso: Registrazione completata.");
        
        return nuovoUtente;
    }
}