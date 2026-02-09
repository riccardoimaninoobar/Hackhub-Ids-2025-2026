package it.unicam.hackhub.presentation;

import it.unicam.hackhub.config.DataLoader;
import it.unicam.hackhub.exception.AuthenticationException;
import it.unicam.hackhub.exception.HackHubException;
import it.unicam.hackhub.exception.ValidationException;
import it.unicam.hackhub.repository.TeamRepository;
import it.unicam.hackhub.repository.UtenteRepository;
import it.unicam.hackhub.service.UtenteService;
import it.unicam.hackhub.service.dto.LoginRequest;
import it.unicam.hackhub.service.dto.RegistrazioneRequest;
import it.unicam.hackhub.service.dto.UtenteResponse;
import it.unicam.hackhub.validation.PasswordValidator;

import java.util.Scanner;

/**
 * Interfaccia a linea di comando per HackHub.
 */
public class HackHubCLI {

    private final Scanner scanner;
    private final UtenteService utenteService;
    private final DataLoader dataLoader;
    private UtenteResponse utenteLoggato;

    public HackHubCLI() {
        this.scanner = new Scanner(System.in);
        UtenteRepository utenteRepository = new UtenteRepository();
        TeamRepository teamRepository = new TeamRepository();
        this.utenteService = new UtenteService(utenteRepository);
        this.dataLoader = new DataLoader(utenteRepository, teamRepository);
    }

    /**
     * Avvia l'interfaccia CLI.
     */
    public void start() {
        printBanner();
        dataLoader.loadData();

        boolean running = true;
        while (running) {
            if (utenteLoggato == null) {
                running = menuPrincipale();
            } else {
                running = menuUtente();
            }
        }

        System.out.println("\nArrivederci!");
        scanner.close();
    }

    private void printBanner() {
        System.out.println("========================================");
        System.out.println("       HACKHUB - Gestione Hackathon     ");
        System.out.println("========================================");
        System.out.println();
    }

    /**
     * Menu per utenti non autenticati.
     */
    private boolean menuPrincipale() {
        System.out.println("\n--- MENU PRINCIPALE ---");
        System.out.println("1. Login");
        System.out.println("2. Registrazione");
        System.out.println("3. Mostra credenziali demo");
        System.out.println("4. Lista utenti registrati");
        System.out.println("0. Esci");
        System.out.print("\nScelta: ");

        String scelta = scanner.nextLine().trim();

        switch (scelta) {
            case "1" -> login();
            case "2" -> registrazione();
            case "3" -> dataLoader.printDemoCredentials();
            case "4" -> listaUtenti();
            case "0" -> { return false; }
            default -> System.out.println("Scelta non valida.");
        }
        return true;
    }

    /**
     * Menu per utenti autenticati.
     */
    private boolean menuUtente() {
        System.out.println("\n--- MENU UTENTE ---");
        System.out.println("Benvenuto, " + utenteLoggato.nome() + " " + utenteLoggato.cognome() + "!");
        System.out.println();
        System.out.println("1. Visualizza profilo");
        System.out.println("2. Lista utenti");
        System.out.println("3. Logout");
        System.out.println("0. Esci");
        System.out.print("\nScelta: ");

        String scelta = scanner.nextLine().trim();

        switch (scelta) {
            case "1" -> visualizzaProfilo();
            case "2" -> listaUtenti();
            case "3" -> logout();
            case "0" -> { return false; }
            default -> System.out.println("Scelta non valida.");
        }
        return true;
    }

    /**
     * Gestisce il login.
     */
    private void login() {
        System.out.println("\n=== LOGIN ===");

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            LoginRequest request = new LoginRequest(username, password);
            utenteLoggato = utenteService.login(request);
            System.out.println("\nLogin effettuato con successo!");
            System.out.println("Benvenuto, " + utenteLoggato.nome() + "!");
        } catch (AuthenticationException e) {
            System.out.println("\nErrore: " + e.getMessage());
        }
    }

    /**
     * Gestisce la registrazione.
     */
    private void registrazione() {
        System.out.println("\n=== REGISTRAZIONE ===");
        System.out.println("Requisiti password: " + PasswordValidator.getRequirements());
        System.out.println();

        System.out.print("Username (min 3 caratteri): ");
        String username = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Cognome: ");
        String cognome = scanner.nextLine().trim();

        try {
            RegistrazioneRequest request = new RegistrazioneRequest(username, email, password, nome, cognome);
            UtenteResponse utente = utenteService.registra(request);

            System.out.println("\nRegistrazione completata con successo!");
            System.out.println("ID: " + utente.id());
            System.out.println("Username: " + utente.username());
            System.out.println("Email: " + utente.email());
            System.out.println("\nOra puoi effettuare il login.");
        } catch (ValidationException e) {
            System.out.println("\nErrore di validazione: " + e.getMessage());
        } catch (HackHubException e) {
            System.out.println("\nErrore: " + e.getMessage());
        }
    }

    /**
     * Effettua il logout.
     */
    private void logout() {
        System.out.println("\nLogout effettuato.");
        utenteLoggato = null;
    }

    /**
     * Visualizza il profilo dell'utente loggato.
     */
    private void visualizzaProfilo() {
        System.out.println("\n=== PROFILO UTENTE ===");
        System.out.println("ID:                " + utenteLoggato.id());
        System.out.println("Username:          " + utenteLoggato.username());
        System.out.println("Email:             " + utenteLoggato.email());
        System.out.println("Nome:              " + utenteLoggato.nome());
        System.out.println("Cognome:           " + utenteLoggato.cognome());
        System.out.println("Data registrazione: " + utenteLoggato.dataRegistrazione());
        if (utenteLoggato.teamNome() != null) {
            System.out.println("Team:              " + utenteLoggato.teamNome());
        } else {
            System.out.println("Team:              Nessuno");
        }
    }

    /**
     * Mostra la lista degli utenti registrati.
     */
    private void listaUtenti() {
        System.out.println("\n=== UTENTI REGISTRATI ===");
        var utenti = utenteService.findAll();

        if (utenti.isEmpty()) {
            System.out.println("Nessun utente registrato.");
            return;
        }

        System.out.printf("%-4s %-15s %-25s %-15s %-15s%n", "ID", "Username", "Email", "Nome", "Cognome");
        System.out.println("-".repeat(80));

        for (UtenteResponse u : utenti) {
            System.out.printf("%-4d %-15s %-25s %-15s %-15s%n",
                u.id(), u.username(), u.email(), u.nome(), u.cognome());
        }

        System.out.println("\nTotale: " + utenti.size() + " utenti");
    }

    /**
     * Entry point per avviare la CLI.
     */
    public static void main(String[] args) {
        HackHubCLI cli = new HackHubCLI();
        cli.start();
    }
}
