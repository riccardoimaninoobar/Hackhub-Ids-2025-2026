package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.application.controller.IscrizioneTeamHandler;

import java.util.Scanner;

public class IscrizioneTeamCLI {

    private final IscrizioneTeamHandler handler;
    private final Scanner scanner;

    public IscrizioneTeamCLI(IscrizioneTeamHandler handler) {
        this.handler = handler;
        this.scanner = new Scanner(System.in);
    }

    // entry point dal menu: l'utente richiede iscriviTeam(teamId)
    public void iscriviTeam(String teamId) {
        System.out.println("\n>>> ISCRIVI TEAM AD HACKATHON <<<");

        // "Inserisci nome dell'Hackathon"
        System.out.print("Inserisci nome dell'Hackathon: ");
        String nomeHackathon = scanner.nextLine();

        try {
            // se la verifica va a buon fine, avvia effettiva iscrizione
            handler.iscriviTeamHandler(nomeHackathon, teamId);

            // mostra "Iscrizione avvenuta con successo"
            System.out.println("Iscrizione avvenuta con successo.");
        } catch (IllegalArgumentException e) {
            // Hackathon inesistente, Team inesistente, ecc.
            if ("Hackathon inesistente".equals(e.getMessage())) {
                System.out.println("Hackathon inesistente.");
            } else {
                mostraErrore(e.getMessage());
            }
        } catch (IllegalStateException e) {
            // Eccezione("Non puoi iscriverti a questo Hackathon")
            mostraErrore(e.getMessage());
        }
    }

    private void mostraErrore(String message) {
        System.out.println("Errore: " + message);
    }
}
