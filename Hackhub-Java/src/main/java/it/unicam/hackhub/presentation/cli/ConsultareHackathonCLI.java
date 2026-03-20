package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.application.controller.ConsultareHackathonHandler;
import it.unicam.hackhub.domain.model.Hackathon;

import java.util.List;
import java.util.Scanner;

public class ConsultareHackathonCLI {

    private final ConsultareHackathonHandler handler;
    private final Scanner scanner;

    public ConsultareHackathonCLI(ConsultareHackathonHandler handler) {
        this.handler = handler;
        this.scanner = new Scanner(System.in);
    }

    // consultaHackathon() chiamato dall'utente generico
    public void consultaHackathon() {
        System.out.println("\n>>> CONSULTA HACKATHON <<<");

        List<Hackathon> lista = handler.getListaHackathon();

        if (lista == null || lista.isEmpty()) {
            mostraErrore();
        } else {
            mostraElenco(lista);
        }
    }

    private void mostraErrore() {
        System.out.println("\n[INFO] Non ci sono Hackathon disponibili al momento.");
    }

    private void mostraElenco(List<Hackathon> lista) {
        System.out.println("\nHackathon disponibili:");
        for (Hackathon h : lista) {
            System.out.println("----------------------------------------");
            System.out.println("Nome: " + h.getNome());
            // qui puoi aggiungere altri dettagli quando avrai i getter
            // es: dataInizio, dataFine, luogo, ecc.
        }
        System.out.println("----------------------------------------");
        System.out.println("\nPremi INVIO per tornare al menu principale...");
        scanner.nextLine();
    }
}
