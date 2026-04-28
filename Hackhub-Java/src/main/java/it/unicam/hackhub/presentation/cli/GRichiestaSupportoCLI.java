package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.application.controller.GRichiestaSupportoHandler;
import it.unicam.hackhub.domain.model.RichiestaSupporto;
import it.unicam.hackhub.domain.model.Utente;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;
@Component
public class GRichiestaSupportoCLI {
    private final GRichiestaSupportoHandler handler;
    private final Scanner scanner = new Scanner(System.in);

    public GRichiestaSupportoCLI(GRichiestaSupportoHandler handler) {
        this.handler = handler;
    }

    public void gestisciRichieste() {
        try {
            List<RichiestaSupporto> richieste = handler.getRichiesteSupporto();
            System.out.println("--- Richieste di supporto ---");
            for (int i = 0; i < richieste.size(); i++) {
                RichiestaSupporto r = richieste.get(i);
                System.out.println(i + "] Team: " + r.getTeam().getNome() + " | " + r.getDescrizione());
            }

            System.out.print("Seleziona una richiesta: ");
            int index = Integer.parseInt(scanner.nextLine());
            RichiestaSupporto richiesta = richieste.get(index);

            System.out.println("Dettagli richiesta: " + richiesta.getDescrizione());
            System.out.print("Scrivi una risposta: ");
            String risposta = scanner.nextLine();

            while (true) {
                try {
                    System.out.print("Inserisci data call (YYYY-MM-DD): ");
                    LocalDate data = LocalDate.parse(scanner.nextLine());
                    System.out.print("Inserisci ora call (HH:MM): ");
                    LocalTime ora = LocalTime.parse(scanner.nextLine());
                    handler.gestisciRichiesta(richiesta, risposta, data, ora);
                    System.out.println("Richiesta gestita con successo.");
                    break;
                } catch (IllegalArgumentException e) {
                    if (!"Slot non disponibile.".equals(e.getMessage())) {
                        throw e;
                    }
                    System.out.println("Slot non disponibile. Inserisci un nuovo slot.");
                }
            }
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    public void visualizzaRichieste(Utente mentore) {
        System.out.println("\n>>> RICHIESTE DI SUPPORTO ASSEGNATE <<<");
        List<RichiestaSupporto> richieste = handler.ottieniRichiestePerMentore(mentore);
        if (richieste.isEmpty()) {
            System.out.println("Nessuna richiesta di supporto aperta trovata per i tuoi hackathon.");
        } else {
            for (RichiestaSupporto r : richieste) {
                System.out.println("- Team: " + r.getTeam().getNome() + " | Oggetto: " + r.getDescrizione());
            }
        }
    }
}
