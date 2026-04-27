package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.application.controller.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class LogoutCLI {
    private final LogoutHandler handler;
    private final Scanner scanner = new Scanner(System.in);

    public LogoutCLI(LogoutHandler handler) {
        this.handler = handler;
    }

    public void richiediLogout() {
        System.out.print("Sei sicuro di voler effettuare il logout? (s/n): ");
        String scelta = scanner.nextLine();

        if (scelta.equalsIgnoreCase("s")) {
            confermaLogout();
        } else {
            annullaLogout();
        }
    }

    public void annullaLogout() {
        System.out.println("Logout annullato.");
    }

    public void confermaLogout() {
        try {
            // Chiamiamo il nuovo metodo unico dell'Handler
            handler.effettuaLogout();
            System.out.println("Logout effettuato con successo. A presto!");
        } catch (IllegalStateException e) {
            // Se l'handler rileva che la sessione era già vuota, catturiamo l'eccezione
            System.out.println("Errore: " + e.getMessage());
        }
    }
}