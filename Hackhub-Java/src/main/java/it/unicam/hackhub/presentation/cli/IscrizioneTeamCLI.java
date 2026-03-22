package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.application.controller.IscrizioneTeamHandler;
import java.util.Scanner;

public class IscrizioneTeamCLI {
    private final IscrizioneTeamHandler handler;
    private final Sessione sessione;
    private final Scanner scanner = new Scanner(System.in);

    public IscrizioneTeamCLI(IscrizioneTeamHandler handler, Sessione sessione) {
        this.handler = handler;
        this.sessione = sessione;
    }

    public void iscriviTeam() {
        if (sessione.getUtenteCorrente() == null || sessione.getUtenteCorrente().getTeam() == null) {
            System.out.println("Errore: Devi essere loggato e far parte di un team per iscriverti.");
            return;
        }

        System.out.println("\n>>> ISCRIVI TEAM AD HACKATHON <<<");
        System.out.print("Inserisci nome dell'Hackathon: ");
        String nomeHackathon = scanner.nextLine();

        try {
            handler.iscriviTeamHandler(nomeHackathon);
            System.out.println("Iscrizione avvenuta con successo.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }
}