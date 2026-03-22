package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.application.controller.ConsultareHackathonHandler;
import it.unicam.hackhub.domain.model.Hackathon;
import java.util.List;
import java.util.Scanner;

public class ConsultareHackathonCLI {
    private final ConsultareHackathonHandler handler;
    private final Scanner scanner = new Scanner(System.in);

    public ConsultareHackathonCLI(ConsultareHackathonHandler handler) {
        this.handler = handler;
    }

    public void consultaHackathon() {
        System.out.println("\n>>> CONSULTA HACKATHON <<<");
        List<Hackathon> lista = handler.getListaHackathon();

        if (lista == null || lista.isEmpty()) {
            System.out.println("\n[INFO] Non ci sono Hackathon disponibili al momento.");
        } else {
            System.out.println("\nHackathon disponibili:");
            for (Hackathon h : lista) {
                System.out.println("- " + h.getNome() + " (Stato: " + h.getStato() + ")");
            }
        }
    }
}