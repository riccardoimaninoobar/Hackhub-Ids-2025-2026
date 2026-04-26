package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.application.controller.IscrizioneTeamHandler;
import it.unicam.hackhub.domain.model.Hackathon;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

@Component
public class IscrizioneTeamCLI {
    private final IscrizioneTeamHandler handler;
    private final Sessione sessione;
    private final Scanner scanner = new Scanner(System.in);

    public IscrizioneTeamCLI(IscrizioneTeamHandler handler, Sessione sessione) {
        this.handler = handler;
        this.sessione = sessione;
    }
    public void richiediIscrizioneTeam() {
        System.out.println("\n>>> ISCRIVI TEAM AD HACKATHON <<<");

        try {
            // 1. Ottieni lista hackathon filtrata
            Set<Hackathon> hs = handler.getHackathonInIscrizione();

            // Blocco [nessun Hackathon in iscrizione]
            if (hs.isEmpty()) {
                System.out.println("Nessun Hackathon disponibile per l'iscrizione.");
                return;
            }

            // 2. Mostra lista e permetti la scelta
            List<Hackathon> hackathonList = new ArrayList<>(hs);
            for (int i = 0; i < hackathonList.size(); i++) {
                System.out.println("[" + i + "] " + hackathonList.get(i).getNome());
            }

            System.out.print("Scegli l'indice dell'Hackathon: ");
            int scelta = Integer.parseInt(scanner.nextLine());
            Hackathon h = hackathonList.get(scelta);

            // 3. Esegui iscrizione
            iscriviTeam(h);
            System.out.println("Iscrizione avvenuta con successo!");

        } catch (IllegalStateException | IllegalArgumentException | IndexOutOfBoundsException e) {
            // Gestione delle eccezioni (es. OperazioneNonConsentita dello stato)
            System.out.println("Errore: " + e.getMessage());
        }
    }
    public void iscriviTeam(Hackathon hackathon) {
        handler.iscriviTeam(hackathon);
    }
}