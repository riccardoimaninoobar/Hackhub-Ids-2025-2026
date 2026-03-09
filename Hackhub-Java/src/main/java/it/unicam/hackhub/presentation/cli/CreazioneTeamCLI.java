package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.application.controller.CreazioneTeamHandler;

import java.util.Scanner;

public class CreazioneTeamCLI {

    private final CreazioneTeamHandler handler;
    private final Scanner scanner;

    public CreazioneTeamCLI(CreazioneTeamHandler handler) {
        this.handler = handler;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Metodo di supporto per richiedere e validare il nome del team.
     * Continua a ciclare finché non viene inserito un nome univoco.
     */
    public String inserisciNomeTeam() {
        String teamName;
        while (true) {
            System.out.print("\n[2] Inserisci il nome del team: ");
            teamName = scanner.nextLine(); 

            System.out.println("[3] SYSTEM verifica che non esista già un team con questo nome...");
            if (handler.verificaTeamEsistente(teamName)) { 
                System.out.println("[3.a.1] Errore: Esiste già un team con il nome '" + teamName + "'.");
                System.out.println("[3.a.2] Il flusso riparte dal punto 2.\n");
                continue; 
            }
            break; // Nome valido e non esistente, esce dal ciclo
        }
        return teamName;
    }

    /**
     * Metodo di supporto per verificare se l'utente può creare un team.
     */
    public boolean verificaUtenteDisponibile(Utente currentUtente) {
        System.out.println("\n[4] SYSTEM verifica che l'Utente non sia già Membro di un altro Team...");
        if (handler.verificaUtenteInTeam(currentUtente)) {
            System.out.println("[4.a.1] Errore: Sei già membro di un altro team.");
            System.out.println("[4.a.2] Creazione team terminata con fallimento.");
            return false;
        }
        return true;
    }

    /**
     * Metodo principale che orchestra il flusso del caso d'uso.
     */
    public void run(Utente currentUtente) {
        System.out.println("[1] L'Utente richiede di creare un nuovo team...");

        // 1. Richiesta ed estrazione del nome del team (gestisce in automatico il ciclo di validazione)
        String teamName = this.inserisciNomeTeam();

        // 2. Controllo di disponibilità dell'utente
        if (!this.verificaUtenteDisponibile(currentUtente)) {
            return; // Interrompe il caso d'uso se l'utente è già in un team
        }

        // 3. Creazione effettiva del Team tramite l'Handler
        System.out.println("\n[5] SYSTEM crea il nuovo team in memoria.");
        System.out.println("[6] SYSTEM assegna l'utente corrente al team come creatore.");
        try {
            Team newTeam = handler.creaTeam(teamName, currentUtente);
            System.out.println("\n-> Team '" + newTeam.getName() + "' creato con successo!");
            System.out.println("-> Sei stato aggiunto come creatore/membro del team.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("\nErrore imprevisto nella creazione del team: " + e.getMessage());
        }
        
        System.out.println("\n[7] Fine Caso d'Uso: Creazione Team completata.");
    }
}