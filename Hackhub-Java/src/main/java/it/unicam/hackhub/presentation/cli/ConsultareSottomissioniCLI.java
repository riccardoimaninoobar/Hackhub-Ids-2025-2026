package it.unicam.hackhub.presentation.cli;
import it.unicam.hackhub.application.controller.ConsultareSottomissioniHandler;
import it.unicam.hackhub.domain.model.Sottomissione;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Scanner;
@Component
public class ConsultareSottomissioniCLI {
    private final ConsultareSottomissioniHandler handler;
    private final Scanner scanner = new Scanner(System.in);
    public ConsultareSottomissioniCLI(ConsultareSottomissioniHandler handler) {
        this.handler = handler;
    }
    public void consultaSottomissioni() {
        System.out.println("\n--- CONSULTA SOTTOMISSIONI ---");
        try {
            System.out.print("Inserisci il nome dell'Hackathon: ");
            String nomeHackathon = scanner.nextLine();
            List<Sottomissione> sottomissioni = handler.getSottomissioniHackathon(nomeHackathon);
            if (sottomissioni.isEmpty()) {
                System.out.println("Non sono presenti sottomissioni per questo Hackathon.");
                return;
            }
            for (int i = 0; i < sottomissioni.size(); i++) {
                Sottomissione s = sottomissioni.get(i);
                System.out.println(i + " - File: " + s.getNomeFile()
                        + " | Team: " + (s.getTeam() != null ? s.getTeam().getNome() : "Team sconosciuto")
                        + " | Link: " + s.getLink()
                        + " | Data caricamento: " + s.getDataCaricamento()
                        + " | Punteggio: " + s.getPunteggio());
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Errore: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Errore inatteso: " + e.getMessage());
        }
    }
}
