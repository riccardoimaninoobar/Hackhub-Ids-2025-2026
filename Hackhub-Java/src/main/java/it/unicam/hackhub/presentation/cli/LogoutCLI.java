package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.application.controller.LogoutHandler;
import java.util.Scanner;

public class LogoutCLI {
    private final LogoutHandler handler;
    private final Scanner scanner = new Scanner(System.in);

    // Costruttore pulito: solo l'handler!
    public LogoutCLI(LogoutHandler handler) {
        this.handler = handler;
    }

    public void richiediLogout() {
        try {
            // La CLI "bussa" all'handler. Se l'handler lancia l'eccezione, si finisce nel catch.
            handler.verificaSessione();
        } catch (IllegalStateException e) {
            System.out.println("Errore: " + e.getMessage());
            return; // Esce subito
        }

        System.out.print("Sei sicuro di voler effettuare il logout? (s/n): ");
        String scelta = scanner.nextLine();

        if (scelta.equalsIgnoreCase("n")) {
            annullaLogout();
        } else if (scelta.equalsIgnoreCase("s")) {
            confermaLogout();
        } else {
            System.out.println("Scelta non valida.");
            annullaLogout();
        }
    }

    public void annullaLogout() {
        System.out.println("Logout annullato.");
    }

    public void confermaLogout() {
        handler.effettuaLogout();
        System.out.println("Logout effettuato.");
    }
}