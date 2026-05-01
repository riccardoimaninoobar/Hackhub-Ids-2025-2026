package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.application.controller.AccettazioneInvitoHandler;
import it.unicam.hackhub.domain.model.invito.Invito;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class AccettazioneInvitoCLI {
    private final AccettazioneInvitoHandler handler;
    private final Scanner scanner = new Scanner(System.in);

    public AccettazioneInvitoCLI(AccettazioneInvitoHandler handler) {
        this.handler = handler;
    }

    // Corrisponde a avviaGestioneInviti() nel diagramma
    public void avviaGestioneInviti() {
        // Chiama l'handler per ottenere gli inviti pendenti
        List<Invito> invitiPendenti = handler.getInvitiPendenti();

        // Corrisponde a mostraListaInviti(invitiPendenti)
        mostraListaInviti(invitiPendenti);

        if (!invitiPendenti.isEmpty()) {
            System.out.print("Seleziona l'indice dell'invito da accettare: ");
            int index = scanner.nextInt();
            if (index >= 0 && index < invitiPendenti.size()) {
                // Corrisponde a selezionaInvito(invito)
                selezionaInvito(invitiPendenti.get(index));
            }
        }
    }

    // Metodo esplicito del diagramma
    private void mostraListaInviti(List<Invito> invitiPendenti) {
        if (invitiPendenti.isEmpty()) {
            System.out.println("Non ci sono inviti pendenti.");
        } else {
            System.out.println("--- Inviti Pendenti ---");
            for (int i = 0; i < invitiPendenti.size(); i++) {
                System.out.println(i + "] Invito dal team: " + invitiPendenti.get(i).getTeamMittente().getNome());
            }
        }
    }

    // Metodo esplicito del diagramma
    private void selezionaInvito(Invito invito) {
        // Inoltra la richiesta all'handler
        handler.accettaInvito(invito);
        System.out.println("Sei entrato nel team!");
    }
}