package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.application.controller.LoginHandler;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class LoginCLI {
    private final LoginHandler handler;
    private final Scanner scanner = new Scanner(System.in);

    public LoginCLI(LoginHandler handler) {
        this.handler = handler;
    }

    public void run() {
        System.out.println("\n>>> EFFETTUA LOGIN <<<");
        System.out.println("Inserire username:");
        String username = scanner.nextLine();
        System.out.println("Inserire password:");
        String password = scanner.nextLine();

        try {
            effettuaLogin(username, password);
            System.out.println("Login effettuato correttamente!");
        } catch (IllegalArgumentException e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }
    public void effettuaLogin(String username, String password) {
        handler.elaboraLogin(username, password);
    }
}