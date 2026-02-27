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
        // STEP 1: L'Utente richiede di inserire un nuovo team
        System.out.println("Creazione Nuovo Team");

        String teamName;
        
        // STEP 2 e 3: L'utente inserisce il nome e il SYSTEM verifica che non esista
        while (true) {
            System.out.print("Inserisci il nome del team: ");
            teamName = scanner.nextLine(); // Step 2

            if (!creazioneTeamHandler.teamExists(teamName)) { // Step 3
                break; // Il nome va bene, esce dal ciclo
            } else {
                // Step 3.a.1 e 3.a.2: Segnala presenza e chiede nuovo nome (il ciclo riprende: 3.a.3)
                System.out.println("Errore: Esiste già un team con il nome '" + teamName + "'.");
                System.out.println("Inserisci un nome differente.");
            }
        }

        // STEP 4: SYSTEM verifica che l'Utente non sia Membro di un altro Team
        if (currentUser.getTeam() != null) {
            // Step 4.a.1: SYSTEM segnala Utente membro Team di un altro Team
            System.out.println("Errore: Sei già membro del team '" + currentUser.getTeam().getName() + "'.");
            System.out.println("Creazione team terminata."); // Step 4.a.2: il caso d'uso termina
            return;
        }

        // STEP 5 e 6: SYSTEM crea il nuovo team e assegna l'utente
        try {
            Team newTeam = creazioneTeamHandler.createTeam(teamName, currentUser);
            System.out.println("Team '" + newTeam.getName() + "' creato con successo!");
            System.out.println("Sei stato aggiunto come membro del team.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Questo blocco cattura eventuali errori di logica sfuggiti, per sicurezza
            System.out.println("Errore imprevisto nella creazione del team: " + e.getMessage());
        }
        
        // STEP 7: il caso d'uso termina
        System.out.println("Fine Creazione Team");
    }
}