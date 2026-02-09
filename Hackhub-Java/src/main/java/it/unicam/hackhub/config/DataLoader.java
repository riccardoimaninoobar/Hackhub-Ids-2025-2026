package it.unicam.hackhub.config;

import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.repository.TeamRepository;
import it.unicam.hackhub.repository.UtenteRepository;

/**
 * Carica dati di esempio nel sistema.
 */
public class DataLoader {

    private final UtenteRepository utenteRepository;
    private final TeamRepository teamRepository;

    public DataLoader(UtenteRepository utenteRepository, TeamRepository teamRepository) {
        this.utenteRepository = utenteRepository;
        this.teamRepository = teamRepository;
    }

    /**
     * Carica i dati demo se il repository è vuoto.
     * @return true se i dati sono stati caricati, false se già presenti
     */
    public boolean loadData() {
        if (utenteRepository.count() > 0) {
            System.out.println("Dati di esempio già presenti, salto il caricamento.");
            return false;
        }

        System.out.println("Caricamento dati di esempio...");

        // Crea utenti con password valide (min 8 char, 1 maiuscola, 1 cifra)
        Utente mario = new Utente("mario", "mario@hackhub.it", "Password1", "Mario", "Rossi");
        Utente luigi = new Utente("luigi", "luigi@hackhub.it", "Password1", "Luigi", "Verdi");
        Utente peach = new Utente("peach", "peach@hackhub.it", "Password1", "Peach", "Bianchi");
        Utente toad = new Utente("toad", "toad@hackhub.it", "Password1", "Toad", "Fungo");
        Utente giudiceUser = new Utente("giudice", "giudice@hackhub.it", "Password1", "Giovanni", "Giudice");
        Utente mentoreUser = new Utente("mentore", "mentore@hackhub.it", "Password1", "Marco", "Mentore");
        Utente anna = new Utente("anna", "anna@hackhub.it", "Password1", "Anna", "Mentor");

        utenteRepository.save(mario);
        utenteRepository.save(luigi);
        utenteRepository.save(peach);
        utenteRepository.save(toad);
        utenteRepository.save(giudiceUser);
        utenteRepository.save(mentoreUser);
        utenteRepository.save(anna);

        // Crea team (solo con i creatori per evitare problemi)
        Team teamAlpha = new Team("Alpha Coders", mario);
        teamRepository.save(teamAlpha);

        Team teamBeta = new Team("Beta Hackers", peach);
        teamRepository.save(teamBeta);

        System.out.println("Caricati " + utenteRepository.count() + " utenti e " + teamRepository.count() + " team");
        return true;
    }

    /**
     * Stampa i dati demo disponibili.
     */
    public void printDemoCredentials() {
        System.out.println("\n=== CREDENZIALI DEMO ===");
        System.out.println("Username: mario    Password: Password1");
        System.out.println("Username: luigi    Password: Password1");
        System.out.println("Username: peach    Password: Password1");
        System.out.println("Username: toad     Password: Password1");
        System.out.println("Username: giudice  Password: Password1");
        System.out.println("Username: mentore  Password: Password1");
        System.out.println("Username: anna     Password: Password1");
        System.out.println("========================\n");
    }
}
