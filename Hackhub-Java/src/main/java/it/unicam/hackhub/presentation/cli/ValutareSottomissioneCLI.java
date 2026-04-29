package it.unicam.hackhub.presentation.cli;
import it.unicam.hackhub.application.controller.ValutareSottomissioneHandler;
import it.unicam.hackhub.domain.model.Hackathon;
import it.unicam.hackhub.domain.model.Sottomissione;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
@Component
public class ValutareSottomissioneCLI {
    private final ValutareSottomissioneHandler handler;
    private final Scanner scanner = new Scanner(System.in);
    public ValutareSottomissioneCLI(ValutareSottomissioneHandler handler) {
        this.handler = handler;
    }
    public void avviaValutazione() {
        System.out.println("\n=== VALUTA SOTTOMISSIONE ===");
        try {
            Set<Hackathon> hackathons = handler.getHackathonsGiudice();
            List<Hackathon> hackathonList = new ArrayList<>(hackathons);
            System.out.println("Seleziona l'Hackathon da valutare:");
            for (int i = 0; i < hackathonList.size(); i++) {
                System.out.println(i + " - " + hackathonList.get(i).getNome());
            }
            System.out.print("Scelta: ");
            int hackathonIndex = Integer.parseInt(scanner.nextLine());
            Hackathon hackathonScelto = hackathonList.get(hackathonIndex);
            Set<Sottomissione> sottomissioni = handler.getSottomissioni(hackathonScelto.getNome());
            List<Sottomissione> sottomissioneList = new ArrayList<>(sottomissioni);
            System.out.println("Sottomissioni disponibili:");
            for (int i = 0; i < sottomissioneList.size(); i++) {
                Sottomissione s = sottomissioneList.get(i);
                String team = s.getTeam() != null ? s.getTeam().getNome() : "Team sconosciuto";
                System.out.println(i + " - " + s.getNomeFile() + " | Team: " + team + " | Punteggio attuale: " + s.getPunteggio());
            }
            System.out.print("Seleziona la sottomissione: ");
            int sottomissioneIndex = Integer.parseInt(scanner.nextLine());
            Sottomissione sottomissioneScelta = sottomissioneList.get(sottomissioneIndex);
            while (true) {
                try {
                    System.out.print("Inserisci un punteggio da 1 a 10: ");
                    int punteggio = Integer.parseInt(scanner.nextLine());
                    handler.assegnaPunteggio(hackathonScelto.getNome(), sottomissioneScelta.getId(), punteggio);
                    System.out.println("Valutazione salvata con successo.");
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Errore: " + e.getMessage());
                }
            }
        } catch (IllegalStateException | IllegalArgumentException | IndexOutOfBoundsException e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }
}
