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
        System.out.println("[1] L'Utente richiede di creare un nuovo team...");

        String teamName;
        
        // Ciclo WHILE per simulare il controllo e l'eventuale reinserimento
        while (true) {
            // STEP 2: L'utente inserisce il nome
            System.out.print("\n[2] Inserisci il nome del team: ");
            teamName = scanner.nextLine(); 

            // STEP 3: SYSTEM verifica che non esista
            System.out.println("[3] SYSTEM verifica che non esista già un team con questo nome...");
            if (creazioneTeamHandler.teamExists(teamName)) { 
                // Step 3.a.1: Segnala presenza 
                System.out.println("[3.a.1] ❌ Errore: Esiste già un team con il nome '" + teamName + "'.");
                // Step 3.a.2: Chiede nuovo nome (il ciclo riprende)
                System.out.println("[3.a.2] Il flusso riparte dal punto 2.\n");
                continue; 
            }
            break; // Il nome va bene, esce dal ciclo
        }

        // STEP 4: SYSTEM verifica che l'Utente non sia Membro di un altro Team
        System.out.println("\n[4] SYSTEM verifica che l'Utente non sia già Membro di un altro Team...");
        if (currentUser.getTeam() != null) {
            // Step 4.a.1: SYSTEM segnala Utente membro di un altro Team
            System.out.println("[4.a.1] ❌ Errore: Sei già membro del team '" + currentUser.getTeam().getName() + "'.");
            // Step 4.a.2: il caso d'uso termina
            System.out.println("[4.a.2] Creazione team terminata con fallimento.");
            return;
        }

        // STEP 5 e 6: SYSTEM crea il nuovo team e assegna l'utente
        System.out.println("\n[5] SYSTEM crea il nuovo team in memoria.");
        System.out.println("[6] SYSTEM assegna l'utente corrente al team.");
        try {
            Team newTeam = creazioneTeamHandler.createTeam(teamName, currentUser);
            System.out.println(" ✅ Team '" + newTeam.getName() + "' creato con successo!");
            System.out.println(" ✅ Sei stato aggiunto come creatore/membro del team.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Sicurezza aggiuntiva per errori di logica
            System.out.println(" ❌ Errore imprevisto nella creazione del team: " + e.getMessage());
        }
        
        // STEP 7: il caso d'uso termina
        System.out.println("\n[7] ✅ Fine Caso d'Uso: Creazione Team completata.");
    }
}