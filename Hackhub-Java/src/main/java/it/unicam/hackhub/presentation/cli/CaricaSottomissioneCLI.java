package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.application.controller.CaricaSottomissioneHandler;
import it.unicam.hackhub.domain.model.hackathon.Hackathon;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

@Component
public class CaricaSottomissioneCLI {

    private final CaricaSottomissioneHandler handler;
    private final Scanner scanner = new Scanner(System.in);

    public CaricaSottomissioneCLI(CaricaSottomissioneHandler handler) {
        this.handler = handler;
    }

    /**
     * Corrisponde a richiediCaricamentoSottomissione() nel diagramma.
     */
    public void richiediCaricamentoSottomissione() {
        System.out.println("\n>>> CARICA SOTTOMISSIONE <<<");

        try {
            // 1. L'Handler recupera gli hackathon "In corso" (tramite Team e Stato)
            Set<Hackathon> hs = handler.getHackathonInCorso();

            // 2. Frammento [break]: se non ci sono hackathon validi, si ferma
            if (hs.isEmpty()) {
                System.out.println("Nessun Hackathon in corso disponibile per il caricamento.");
                return;
            }

            // 3. Mostra l'elenco e permette la selezione (Seleziona Hackathon)
            List<Hackathon> hackathonList = new ArrayList<>(hs);
            System.out.println("Seleziona l'Hackathon:");
            for (int i = 0; i < hackathonList.size(); i++) {
                System.out.println("[" + i + "] " + hackathonList.get(i).getNome());
            }

            System.out.print("Scelta: ");
            int index = Integer.parseInt(scanner.nextLine());
            Hackathon hackathonScelto = hackathonList.get(index);

            // 4. Richiesta dati sottomissione
            System.out.print("Inserisci link/percorso sottomissione: ");
            String link = scanner.nextLine();

            // 5. Delega all'handler per l'operazione di business
            handler.caricaSottomissione(hackathonScelto, link);

            System.out.println("Sottomissione caricata correttamente!");

        } catch (IllegalStateException | IllegalArgumentException | IndexOutOfBoundsException e) {
            // Gestione errori (es. OperazioneNonConsentita dello stato)
            System.out.println("Errore: " + e.getMessage());
        }
    }
}