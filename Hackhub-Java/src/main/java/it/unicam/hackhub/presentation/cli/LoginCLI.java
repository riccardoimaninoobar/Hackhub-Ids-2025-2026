package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.application.controller.LoginHandler;
import it.unicam.hackhub.domain.model.Utente;

import java.util.Scanner;

public class LoginCLI {
    private final LoginHandler handler;
    private final Scanner scanner = new Scanner(System.in);

    public LoginCLI(LoginHandler handler) {
        this.handler = handler;
    }

    public Utente run() {
        System.out.println("Inserire username:");
        String username = scanner.nextLine();
        System.out.println("Inserire password:");
        String password = scanner.nextLine();
        Utente utente = effettuaLogin(username, password);
        if(utente != null) {
            System.out.println("Utente loggato");
        }
        return utente;
    }
    public Utente effettuaLogin(String username, String password) {
        try {
            return handler.elaboraLogin(username, password);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
