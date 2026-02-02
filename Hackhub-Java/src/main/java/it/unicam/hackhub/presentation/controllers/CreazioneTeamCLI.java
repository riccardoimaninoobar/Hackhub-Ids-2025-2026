package it.unicam.hackhub.presentation.controllers;

import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.User;
import it.unicam.hackhub.service.CreazioneTeamHandler;

import java.util.Scanner;

public class CreazioneTeamCLI {

    private final CreazioneTeamHandler creazioneTeamHandler;
    private final Scanner scanner;

    public CreazioneTeamCLI(CreazioneTeamHandler creazioneTeamHandler) {
        this.creazioneTeamHandler = creazioneTeamHandler;
        this.scanner = new Scanner(System.in);
    }

    public void createTeam(User currentUser) {
        System.out.println("Creazione Nuovo Team");

        if (currentUser.getTeam() != null) {
            System.out.println("Errore: Sei già membro del team '" + currentUser.getTeam().getName() + "'.");
            System.out.println("Creazione team terminata.");
            return;
        }

        String teamName;
        while (true) {
            System.out.print("Inserisci il nome del team: ");
            teamName = scanner.nextLine();

            if (!creazioneTeamHandler.teamExists(teamName)) {
                break;
            } else {
                System.out.println("Errore: Esiste già un team con il nome '" + teamName + "'.");
                System.out.println("Inserisci un nome differente.");
            }
        }

        try {
            Team newTeam = creazioneTeamHandler.createTeam(teamName, currentUser);
            System.out.println("Team '" + newTeam.getName() + "' creato con successo!");
            System.out.println("Sei stato aggiunto come membro del team.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Errore nella creazione del team: " + e.getMessage());
        }
        System.out.println("Fine Creazione Team ");
    }
}
