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

        while (true) {
            System.out.println("\n[2] Il Visitatore inserisce i dati di registrazione (username, e-mail e password).");

            System.out.print(" -> Username: ");
            String username = scanner.nextLine();

            System.out.print(" -> E-mail: ");
            String email = scanner.nextLine();

            System.out.print(" -> Password: ");
            String password = scanner.nextLine();

            try {
                return effettuaRegistrazione(username, email, password);
            } catch (IllegalArgumentException e) {
                gestisciErrore(e);
            }
        }
    }
    private Utente effettuaRegistrazione(String username, String email, String password) {
        System.out.println("\n[3] SYSTEM elabora la registrazione...");

        Utente nuovoUtente = handler.elaboraRegistrazione(username, email, password);

        System.out.println("[5] SYSTEM crea il nuovo utente.");
        System.out.println("Registrazione completata con successo! Benvenuto " + nuovoUtente.getUsername() + ".");
        System.out.println("[6] Il caso d'uso termina.");

        return nuovoUtente;
    }
    private void gestisciErrore(IllegalArgumentException e) {
        String message = e.getMessage();

        if ("Esiste già un utente con lo stesso username o e-mail.".equals(message)) {
            System.out.println("[4.a.1] SYSTEM segnala presenza utente con stesso username e/o e-mail.");
            System.out.println("[4.a.2] Il Visitatore modifica username e/o e-mail.");
            System.out.println("[4.a.3] Il flusso riprende dal punto 4.\n");
        } else {
            System.out.println("[3.a.1] SYSTEM segnala errore nella compilazione dei campi: " + message);
            System.out.println("[3.a.2] Il Visitatore corregge la compilazione.");
            System.out.println("[3.a.3] Il flusso riprende dal punto 3.\n");
        }
    }
}