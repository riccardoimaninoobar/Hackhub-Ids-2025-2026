package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.application.controller.ProclamaVincitoreHandler;
import it.unicam.hackhub.domain.model.Utente;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
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
            // Aggiornato il prompt testuale e la lettura della stringa
            System.out.print("Inserisci il nome dell'Hackathon: ");
            String nomeHackathon = scanner.nextLine();

            // PASSO 1 e 2
            List<String> valutazioni = handler.getValutazioniTeam(nomeHackathon);
            System.out.println("\nValutazioni del Giudice:");
            for (String val : valutazioni) {
                System.out.println("- " + val);
            }

            // PASSO 3: Aggiornato il prompt testuale e la lettura della stringa
            System.out.print("\nInserisci il nome del Team da proclamare vincitore: ");
            String nomeTeam = scanner.nextLine();

            // PASSO 4, 5, 6, 7
            boolean successo = handler.proclamaVincitore(nomeHackathon, nomeTeam);

            // PASSO 8 (e 5.a.3)
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