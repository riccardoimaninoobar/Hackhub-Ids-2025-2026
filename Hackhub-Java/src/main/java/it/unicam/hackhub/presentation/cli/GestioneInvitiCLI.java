package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.application.controller.GestioneInvitiHandler;
import java.util.Scanner;

public class GestioneInvitiCLI {

    private final GestioneInvitiHandler handler;
    private final Scanner scanner = new Scanner(System.in);

    public GestioneInvitiCLI(GestioneInvitiHandler handler) {
        this.handler = handler;
    }

    public void run() {
        // Controllo preventivo: se non loggato o senza team, esce subito
        try {
            handler.checkPrerequisiti();
        } catch (IllegalStateException e) {
            System.out.println("Errore: " + e.getMessage());
            return;
        }

        System.out.println("\n>>> INVITA UN UTENTE NEL TUO TEAM <<<");

        // Loop: finché dati non validi
        while (true) {
            System.out.print("Inserisci l'username da invitare (o '0' per annullare): ");
            String username = scanner.nextLine();

            if ("0".equals(username)) {
                System.out.println("Operazione annullata.");
                break;
            }

            try {
                // Inoltra la chiamata come nel diagramma
                inserisciUsername(username);

                // Se arriva qui, il Successo è confermato
                System.out.println("Invio effettuato correttamente!");
                break; // Uscita dal loop

            } catch (IllegalArgumentException e) {
                // Gestione del blocco ALT
                System.out.println("Errore: " + e.getMessage());
            }
        }
    }

    // Metodo visibile nel Sequence Diagram (freccia d'ingresso dall'Utente)
    public void inserisciUsername(String username) {
        handler.elaboraInvito(username);
    }
}