package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.application.controller.RichiestaSupportoHandler;
import it.unicam.hackhub.domain.model.Hackathon;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

@Component
public class RichiestaSupportoCLI {
    private final RichiestaSupportoHandler handler;
    private final Scanner scanner = new Scanner(System.in);

    public RichiestaSupportoCLI(RichiestaSupportoHandler handler) {
        this.handler = handler;
    }

    // =====================================================================
    // 1. PRIMA FRECCIA DALL'ATTORE ALLA CLI: avviaRichiestaSupporto()
    // =====================================================================
    public void avviaRichiestaSupporto() {
        System.out.println("\n>>> INVIA RICHIESTA DI SUPPORTO <<<");

        try {
            // Chiamata all'Handler come da diagramma
            Set<Hackathon> hs = handler.getHackathons();

            // "elenco hackathon a cui il team è iscritto" (Freccia di ritorno)
            List<Hackathon> hackathonList = new ArrayList<>(hs);
            System.out.println("Seleziona l'Hackathon per cui richiedere supporto:");
            for (int i = 0; i < hackathonList.size(); i++) {
                System.out.println(i + "] " + hackathonList.get(i).getNome());
            }

            System.out.print("Scelta: ");
            int index = Integer.parseInt(scanner.nextLine());
            Hackathon hackathonScelto = hackathonList.get(index);

            // Trigger della seconda azione dell'utente
            richiediSupporto(hackathonScelto);

        } catch (IllegalStateException e) {
            System.out.println("Errore: " + e.getMessage());
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            System.out.println("Errore: Selezione non valida.");
        }
    }

    // =====================================================================
    // 2. SECONDA FRECCIA DALL'ATTORE ALLA CLI: getHackathons(h)
    // =====================================================================
    public void richiediSupporto(Hackathon h) {
        // Freccia di ritorno: "Inserire descrizione problema"
        System.out.println("\nInserire descrizione problema (min. 20 caratteri):");

        // Inizio del frammento LOOP
        while (true) {
            System.out.print("-> ");
            String desc = scanner.nextLine();

            // L'utente inserisce la descrizione (Terza azione)
            boolean successo = inserisciDescrizioneProblema(h, desc);

            if (successo) {
                break; // Guardia [descrizione >= 20 caratteri] - Esce dal loop
            }
        }
    }

    // =====================================================================
    // 3. TERZA FRECCIA DALL'ATTORE ALLA CLI (Nel Loop): inserisciDescrizioneProblema(desc)
    // =====================================================================
    public boolean inserisciDescrizioneProblema(Hackathon h, String desc) {
        try {
            // La CLI delega la validazione all'Handler
            handler.convalidaDescrizione(desc);

            // --- BLOCCO BREAK (Successo) ---
            // Se non vengono lanciate eccezioni, registra la richiesta
            // (Nota: passiamo anche 'h' all'handler per permettergli di salvarlo)
            handler.registraRichiestaSupporto(h, desc);

            // Freccia di ritorno finale: "Richiesta supporto inserita correttamente"
            System.out.println("Richiesta supporto inserita correttamente!");
            return true; // Segnala al loop di interrompersi

        } catch (IllegalArgumentException e) {
            // --- ECCEZIONE (Fallimento validazione) ---
            // Freccia di ritorno: "Descrizione troppo breve"
            System.out.println("Descrizione troppo breve: " + e.getMessage());
            return false; // Segnala al loop di continuare
        }
    }
}