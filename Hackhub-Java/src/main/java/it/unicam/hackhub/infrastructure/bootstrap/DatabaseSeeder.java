package it.unicam.hackhub.infrastructure.bootstrap;

import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.model.hackathon.Hackathon;
import it.unicam.hackhub.domain.model.hackathon.HackathonBuilder;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.TeamRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@Profile("!test")
public class DatabaseSeeder implements CommandLineRunner {

    private final HackathonRepository hackathonRepo;
    private final UtenteRepository utenteRepo;
    private final TeamRepository teamRepo;

    public DatabaseSeeder(HackathonRepository hackathonRepo, UtenteRepository utenteRepo, TeamRepository teamRepo) {
        this.hackathonRepo = hackathonRepo;
        this.utenteRepo = utenteRepo;
        this.teamRepo = teamRepo;
    }

    @Override
    @Transactional // Garantisce la coerenza delle operazioni sul DB
    public void run(String... args) throws Exception {
        System.out.println("Verifica integrità dati iniziali...");

        // 1. Gestione Utenti (Usa findByUsername per evitare duplicati)
        Utente o = utenteRepo.findByUsername("organizzatore")
                .orElseGet(() -> utenteRepo.save(new Utente("organizzatore", "org@hack.it", "pass123")));

        Utente g = utenteRepo.findByUsername("AnnaGiudice")
                .orElseGet(() -> utenteRepo.save(new Utente("AnnaGiudice", "anna@hack.it", "pass123")));

        Utente m = utenteRepo.findByUsername("LuigiMentore")
                .orElseGet(() -> utenteRepo.save(new Utente("LuigiMentore", "luigi@hack.it", "pass123")));

        Utente membroTeam = utenteRepo.findByUsername("membroTeam")
                .orElseGet(() -> utenteRepo.save(new Utente("membroTeam", "membro@hack.it", "pass123")));

        // 2. Gestione Team
        Team team = teamRepo.findByNome("teamPartecipante")
                .orElseGet(() -> teamRepo.save(new Team("teamPartecipante")));

        // Verifica che l'utente sia nel team
        if (!team.isMembro(membroTeam)) {
            team.aggiungiMembro(membroTeam);
            utenteRepo.save(membroTeam); // Aggiorna la relazione nel DB
        }

        // 3. Gestione Hackathon
        if (!hackathonRepo.existsByNome("hackProva")) {
            Hackathon hackathon = new HackathonBuilder()
                    .assegnaNome("hackProva")
                    .assegnaRegolamento("Regolamento di prova")
                    .assegnaScadenza(LocalDate.now().plusDays(10))
                    .assegnaDataInizio(LocalDate.now().plusDays(15))
                    .assegnaDataFine(LocalDate.now().plusDays(20))
                    .assegnaLuogo("Camerino")
                    .assegnaDimMaxTeam(5)
                    .assegnaPremioImporto(new BigDecimal("1000"))
                    .assegnaOrganizzatore(o)
                    .assegnaGiudice(g)
                    .assegnaMentore(m)
                    .build();

            // 4. Aggiunta della partecipazione
            hackathon.aggiungiTeam(team);
            hackathonRepo.save(hackathon);
            System.out.println("Hackathon 'hackProva' creato e team iscritto.");
        }

        System.out.println("Procedura di seeding completata.");
    }
}