package it.unicam.hackhub.presentation;

import it.unicam.hackhub.application.controller.CreazioneHackathonHandler;

import java.sql.SQLOutput;
import java.time.LocalDate;
import java.util.Scanner;

public class CreazioneHackathonCLI {
    private final CreazioneHackathonHandler handler;
    private final Scanner scanner = new Scanner(System.in);
    public CreazioneHackathonCLI(CreazioneHackathonHandler handler) {
        this.handler = handler;
    }
    public void run() {
        System.out.println("Inserisci nome Hackathon");
        String nome = scanner.nextLine();
        System.out.println("Inserisci regolamento Hackathon");
        String regolamento = scanner.nextLine();
        System.out.println("Inserisci data scadenza iscrizioni Hackathon");
        LocalDate scadenzaIscrizioni = LocalDate.parse(scanner.nextLine());
        System.out.println("Inserisci data inizio Hackathon");
        LocalDate dataInizio = LocalDate.parse(scanner.nextLine());
        System.out.println("Inserisci data fine Hackathon");
        LocalDate dataFine = LocalDate.parse(scanner.nextLine());
        System.out.println("Inserisci luogo Hackathon");
        String luogo = scanner.nextLine();
        System.out.println("Inserisci dimensione massima team Hackathon");
        Integer dimMaxTeam = Integer.valueOf(scanner.nextLine());

    }
}
