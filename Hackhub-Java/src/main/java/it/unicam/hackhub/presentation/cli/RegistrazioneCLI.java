package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.application.controller.RegistrazioneHandler;
import java.util.Scanner;

public class RegistrazioneCLI {
    private final RegistrazioneHandler handler;
    private final Scanner scanner;

    public RegistrazioneCLI(RegistrazioneHandler handler) {
        this.handler = handler;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("\n[1] Il Visitatore richiede di registrarsi al sistema...");
        while (true) {
            System.out.println("\n[2] Inserisci i dati di registrazione:");
            System.out.print(" -> Username: ");
            String username = scanner.nextLine();
            System.out.print(" -> E-mail: ");
            String email = scanner.nextLine();
            System.out.print(" -> Password: ");
            String password = scanner.nextLine();

            try {
                effettuaRegistrazione(username, email, password);

                System.out.println("Registrazione effettuata correttamente!");
                System.out.println("Benvenuto " + username + " (Autologin eseguito).");
                break; // Esce dal loop di inserimento
            } catch (IllegalArgumentException e) {
                System.out.println("Errore: " + e.getMessage());
                System.out.println("Riprova la compilazione.\n");
            }
        }
    }

    public void effettuaRegistrazione(String username, String email, String password) {
        // La CLI inoltra la chiamata all'Handler
        handler.elaboraRegistrazione(username, email, password);
    }
}