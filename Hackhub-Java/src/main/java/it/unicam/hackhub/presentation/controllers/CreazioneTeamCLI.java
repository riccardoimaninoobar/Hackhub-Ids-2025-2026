package it.unicam.hackhub.presentation.controllers;

import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.service.CreazioneTeamHandler;

import java.util.Scanner;

public class CreazioneTeamCLI {

    private final CreazioneTeamHandler creazioneTeamHandler;
    private final Scanner scanner;

    public CreazioneTeamCLI(CreazioneTeamHandler creazioneTeamHandler) {
        this.creazioneTeamHandler = creazioneTeamHandler;
        this.scanner = new Scanner(System.in);
    }

    public void createTeam(Utente currentUtente) {
        System.out.println("[1] L'Utente richiede di creare un nuovo team...");

        String teamName;
        
        while (true) {
            System.out.print("\n[2] Inserisci il nome del team: ");
            teamName = scanner.nextLine(); 

            System.out.println("[3] SYSTEM verifica che non esista già un team con questo nome...");
            // MODIFICA: Uso il nuovo nome del metodo
            if (creazioneTeamHandler.verificaTeamEsistente(teamName)) { 
                System.out.println("[3.a.1] ❌ Errore: Esiste già un team con il nome '" + teamName + "'.");
                System.out.println("[3.a.2] Il flusso riparte dal punto 2.\n");
                continue; 
            }
            break; 
        }

        System.out.println("\n[4] SYSTEM verifica che l'Utente non sia già Membro di un altro Team...");
        // MODIFICA: Uso il metodo dell'Handler come richiesto dal diagramma delle classi!
        if (creazioneTeamHandler.verificaUtenteInTeam(currentUtente)) {
            System.out.println("[4.a.1] ❌ Errore: Sei già membro di un altro team.");
            System.out.println("[4.a.2] Creazione team terminata con fallimento.");
            return;
        }

        System.out.println("\n[5] SYSTEM crea il nuovo team in memoria.");
        System.out.println("[6] SYSTEM assegna l'utente corrente al team.");
        try {
            // MODIFICA: Uso il nuovo nome del metodo
            Team newTeam = creazioneTeamHandler.creaTeam(teamName, currentUtente);
            System.out.println(" ✅ Team '" + newTeam.getName() + "' creato con successo!");
            System.out.println(" ✅ Sei stato aggiunto come creatore/membro del team.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(" ❌ Errore imprevisto nella creazione del team: " + e.getMessage());
        }
        
        System.out.println("\n[7] ✅ Fine Caso d'Uso: Creazione Team completata.");
    }
}