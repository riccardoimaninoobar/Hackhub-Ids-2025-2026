package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.application.controller.CreazioneTeamHandler;
import it.unicam.hackhub.domain.model.Team;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class CreazioneTeamCLI {
    private final CreazioneTeamHandler handler;
    private final Sessione sessione;
    private final Scanner scanner = new Scanner(System.in);

    public CreazioneTeamCLI(CreazioneTeamHandler handler, Sessione sessione) {
        this.handler = handler;
        this.sessione = sessione;
    }

    public void run() {
        if (sessione.getUtenteCorrente() == null) {
            System.out.println("Errore: Devi effettuare il login per creare un team.");
            return;
        }

        String teamName;
        while (true) {
            System.out.print("\nInserisci il nome del team: ");
            teamName = scanner.nextLine();
            if (handler.verificaTeamEsistente(teamName)) {
                System.out.println("Errore: Esiste già un team con il nome '" + teamName + "'.");
                continue;
            }
            break;
        }

        try {
            Team newTeam = handler.creaTeam(teamName, null);
            System.out.println("\n-> Team '" + newTeam.getNome() + "' creato con successo!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Errore imprevisto: " + e.getMessage());
        }
    }
}