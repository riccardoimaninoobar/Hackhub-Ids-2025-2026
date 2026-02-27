package it.unicam.hackhub.presentation;

import it.unicam.hackhub.domain.model.User;
import it.unicam.hackhub.presentation.controllers.CreazioneTeamCLI;
import it.unicam.hackhub.service.CreazioneTeamHandler;

public class Main {
    public static void main(String[] args) {
        CreazioneTeamHandler handler = new CreazioneTeamHandler();
        User mario = new User("mario", "m@test");
        User luigi = new User("luigi", "l@test");

        System.out.println("--- AUTO TEST (VERIFICA LOGICA) ---");

        // 1. Mario crea TeamAlpha (Deve funzionare)
        try {
            handler.createTeam("TeamAlpha", mario);
            System.out.println("Test 1: OK");
        } catch (Exception e) { System.out.println("Test 1: FALLITO " + e.getMessage()); }

        // 2. Luigi crea TeamAlpha (Deve dare errore)
        try {
            handler.createTeam("TeamAlpha", luigi);
            System.out.println("Test 2: FALLITO (Doveva dare errore)");
        } catch (Exception e) { System.out.println("Test 2: OK (Errore catturato)"); }

        // 3. Mario crea altro team (Deve dare errore)
        try {
            handler.createTeam("TeamBeta", mario);
            System.out.println("Test 3: FALLITO (Mario ha gia' un team)");
        } catch (Exception e) { System.out.println("Test 3: OK (Errore catturato)"); }

        
        System.out.println("\n--- TEST MANUALE (VERIFICA FLUSSO CLI) ---");
        CreazioneTeamCLI cli = new CreazioneTeamCLI(handler);

        // TEST CLI A: Flusso standard e controllo nome duplicato (Step 2, 3, 5, 6)
        System.out.println("\n>>> PROVA 1 (Utente nuovo):");
        System.out.println("Istruzioni: Inserisci 'TeamAlpha' (darà errore), poi 'TeamGamma' (ok).");
        cli.createTeam(new User("anna", "a@test"));

        // TEST CLI B: Controllo utente già in un team (Step 4)
        System.out.println("\n>>> PROVA 2 (Utente già in un team):");
        System.out.println("Istruzioni: Inserisci 'TeamZeta'. Il sistema lo accetterà, ma poi ti bloccherà perché Mario ha già un team.");
        cli.createTeam(mario); // Mario ha già creato TeamAlpha nel Test 1 automatico
    }
}
