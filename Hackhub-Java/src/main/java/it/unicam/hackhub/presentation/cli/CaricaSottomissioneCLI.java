package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.application.controller.CaricaSottomissioneHandler;
import it.unicam.hackhub.domain.model.Team;

import java.util.Scanner;

public class CaricaSottomissioneCLI {

    private final CaricaSottomissioneHandler handler;
    private final Scanner scanner;

    public CaricaSottomissioneCLI(CaricaSottomissioneHandler handler) {
        this.handler = handler;
        this.scanner = new Scanner(System.in);
    }

    // entry point: Membro team richiede di caricare una sottomissione
    public void caricaSottomissione(Team teamUtente) {
        System.out.println("\n>>> CARICA SOTTOMISSIONE <<<");

        // 2. SYSTEM richiede il nome dell'hackathon
        String nomeHackathon = inserisciNomeHackathon();

        try {
            // 4. verifica che lo stato dell'hackathon sia "in corso"
            String stato = handler.verificaStato(nomeHackathon);
            if (!"in corso".equalsIgnoreCase(stato)) {
                System.out.println("L'hackathon non è in corso: non puoi caricare sottomissioni.");
                return;
            }

            // 5. SYSTEM richiede al Membro team di caricare un file
            String[] fileDettagli = inserisciFileDettagli();

            // 6. carica il file con team
            handler.caricamentoSottomissione(nomeHackathon, 
                                           fileDettagli[0], fileDettagli[1], teamUtente);

            // 7. SYSTEM avvisa che la sottomissione è stata caricata con successo
            System.out.println("Sottomissione caricata con successo!");

        } catch (IllegalArgumentException e) {
            // 3.a: hackathon non esiste
            System.out.println(e.getMessage());
            // riprende dal punto 2
            caricaSottomissione(teamUtente);
        } catch (IllegalStateException e) {
            // 4.a: non può più caricare
            System.out.println(e.getMessage());
        }
    }

    private String inserisciNomeHackathon() {
        System.out.print("Inserisci nome dell'hackathon: ");
        return scanner.nextLine();
    }

    private String[] inserisciFileDettagli() {
        System.out.print("Inserisci nome del file: ");
        String nomeFile = scanner.nextLine();
        System.out.print("Inserisci link del file: ");
        String link = scanner.nextLine();
        return new String[]{nomeFile, link};
    }
}