package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.application.controller.ProclamaVincitoreHandler;
import it.unicam.hackhub.domain.model.Utente;
import java.util.List;
import java.util.Scanner;

public class ProclamaVincitoreCLI {

    private final ProclamaVincitoreHandler handler;
    private final Sessione sessioneApp;
    private final Scanner scanner;

    public ProclamaVincitoreCLI(ProclamaVincitoreHandler handler, Sessione sessioneApp) {
        this.handler = handler;
        this.sessioneApp = sessioneApp;
        this.scanner = new Scanner(System.in);
    }

    public void avviaMenu() {
        Utente utenteLoggato = sessioneApp.getUtenteCorrente();
        if (utenteLoggato == null) {
            System.out.println("Errore: Devi essere loggato come Organizzatore per proclamare un vincitore.");
            return;
        }

        try {
            System.out.println("--- PROCLAMA VINCITORE ---");
            System.out.print("Inserisci l'ID dell'Hackathon: ");
            int idHackathon = Integer.parseInt(scanner.nextLine());

            // PASSO 1 e 2: L'Organizzatore richiede di visualizzare le valutazioni e il sistema le mostra
            List<String> valutazioni = handler.getValutazioniTeam(idHackathon);
            System.out.println("\nValutazioni del Giudice:");
            for (String val : valutazioni) {
                System.out.println("- " + val);
            }

            // PASSO 3: L'Organizzatore seleziona il team vincitore
            System.out.print("\nInserisci l'ID del Team da proclamare vincitore: ");
            int idTeam = Integer.parseInt(scanner.nextLine());

            // PASSO 4, 5, 6, 7: Il sistema elabora la proclamazione e l'erogazione del premio
            boolean successo = handler.proclamaVincitore(idHackathon, idTeam);

            // PASSO 8 (e 5.a.3): Feedback finale
            if (successo) {
                System.out.println("Hackathon concluso con successo. Team proclamato e premio erogato.");
            } else {
                System.out.println("Errore nell'erogazione del premio. La proclamazione è stata annullata. Riprovare più tardi.");
            }

        } catch (Exception e) {
            System.err.println("Errore: " + e.getMessage());
        }
    }
}