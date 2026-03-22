package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.application.controller.AggiungiMentoreHandler;
import java.util.Scanner;

public class AggiungiMentoreCLI {
    private final AggiungiMentoreHandler handler;
    private final Sessione sessione;
    private final Scanner scanner = new Scanner(System.in);

    public AggiungiMentoreCLI(AggiungiMentoreHandler handler, Sessione sessione) {
        this.handler = handler;
        this.sessione = sessione;
    }

    public void run() {
        if (sessione.getUtenteCorrente() == null) {
            System.out.println("Errore: Devi effettuare il login.");
            return;
        }

        System.out.println("\n>>> AGGIUNGI MENTORE <<<");
        System.out.print("Inserisci nome dell'hackathon: ");
        String nomeHackathon = scanner.nextLine();

        try {
            handler.checkOrg(nomeHackathon);
        } catch (Exception e) {
            System.out.println("Errore Autorizzazione: " + e.getMessage());
            return;
        }

        while (true) {
            System.out.print("Inserire username del mentore da aggiungere: ");
            String username = scanner.nextLine();
            try {
                handler.aggiungiMentore(username);
                System.out.println("Mentore aggiunto correttamente!");
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Errore: " + e.getMessage());
            }
        }
    }
}