package it.unicam.hackhub.infrastructure.bootstrap;

import it.unicam.hackhub.domain.model.*;
import it.unicam.hackhub.domain.model.hackathon.Hackathon;
import it.unicam.hackhub.domain.model.hackathon.HackathonBuilder;
import it.unicam.hackhub.domain.model.hackathon.state.*;
import it.unicam.hackhub.domain.model.invito.Invito;
import it.unicam.hackhub.domain.model.invito.state.StatoPendente;
import it.unicam.hackhub.domain.repository.*;
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
    private final InvitoRepository invitoRepo;
    private final RichiestaSupportoRepository richiestaRepo;
    private final SegnalazioneRepository segnalazioneRepo;

    // Iniettiamo tutte le repository necessarie per preparare i casi d'uso
    public DatabaseSeeder(HackathonRepository hackathonRepo, UtenteRepository utenteRepo,
                          TeamRepository teamRepo, InvitoRepository invitoRepo,
                          RichiestaSupportoRepository richiestaRepo, SegnalazioneRepository segnalazioneRepo) {
        this.hackathonRepo = hackathonRepo;
        this.utenteRepo = utenteRepo;
        this.teamRepo = teamRepo;
        this.invitoRepo = invitoRepo;
        this.richiestaRepo = richiestaRepo;
        this.segnalazioneRepo = segnalazioneRepo;
    }

    @Override
    @Transactional // Garantisce la coerenza delle operazioni sul DB
    public void run(String... args) throws Exception {
        System.out.println("Inizializzazione dati DB...");

        // 1. UTENTI (Staff e Partecipanti)
        Utente org = creaUtente("organizzatore", "org@hack.it", "pass123");
        Utente giudice = creaUtente("giudice", "giudice@hack.it", "pass123");
        Utente mentore = creaUtente("mentore", "mentore@hack.it", "pass123");
        Utente leaderA = creaUtente("leaderA", "leada@hack.it", "pass123");
        Utente leaderB = creaUtente("leaderB", "leadb@hack.it", "pass123");
        Utente utenteSolo = creaUtente("utenteSolo", "solo@hack.it", "pass123"); // Per testare gli inviti

        // 2. TEAM
        Team teamA = teamRepo.findByNome("TeamAlpha").orElseGet(() -> teamRepo.save(new Team("TeamAlpha")));
        teamA.setDatiBancari("IT123456789012345678901234567");
        aggiungiMembro(teamA, leaderA);

        Team teamB = teamRepo.findByNome("TeamBeta").orElseGet(() -> teamRepo.save(new Team("TeamBeta")));
        aggiungiMembro(teamB, leaderB);

        // 3. HACKATHON IN ISCRIZIONE (Per testare la registrazione dei team)
        if (!hackathonRepo.existsByNome("Hack Iscrizione")) {
            Hackathon hIscrizione = new HackathonBuilder()
                    .assegnaNome("Hack Iscrizione")
                    .assegnaRegolamento("Regolamento iscrizione aperta")
                    .assegnaScadenza(LocalDate.now().plusDays(5)) // Scadenza nel futuro
                    .assegnaDataInizio(LocalDate.now().plusDays(10))
                    .assegnaDataFine(LocalDate.now().plusDays(15))
                    .assegnaLuogo("Roma")
                    .assegnaOrganizzatore(org).assegnaGiudice(giudice).assegnaMentore(mentore)
                    .assegnaDimMaxTeam(5).assegnaPremioImporto(new BigDecimal("1000")).build();
            hIscrizione.aggiornaStato(); // Passerà a InIscrizione
            hackathonRepo.save(hIscrizione);
        }

        // 4. HACKATHON IN CORSO (Per testare le sottomissioni e le richieste di supporto)
        Hackathon hInCorso = null;
        if (!hackathonRepo.existsByNome("Hack In Corso")) {
            hInCorso = new HackathonBuilder()
                    .assegnaNome("Hack In Corso")
                    .assegnaRegolamento("Regolamento hackathon attivo")
                    .assegnaScadenza(LocalDate.now().minusDays(5)) // Iscrizioni chiuse
                    .assegnaDataInizio(LocalDate.now().minusDays(2)) // Iniziato da 2 giorni
                    .assegnaDataFine(LocalDate.now().plusDays(5))    // Finisce tra 5 giorni
                    .assegnaLuogo("Milano")
                    .assegnaOrganizzatore(org).assegnaGiudice(giudice).assegnaMentore(mentore)
                    .assegnaDimMaxTeam(5).assegnaPremioImporto(new BigDecimal("2000")).build();

            // Iscriviamo i team prima di forzare l'inizio
            hInCorso.setStato(new StatoInIscrizione());
            hInCorso.aggiungiTeam(teamA);
            hInCorso.aggiungiTeam(teamB);
            hInCorso.setStato(new StatoInCorso()); // Forziamo lo stato in corso
            hInCorso = hackathonRepo.save(hInCorso);
        } else {
            hInCorso = hackathonRepo.findByNome("Hack In Corso").get();
        }

        // 5. HACKATHON IN VALUTAZIONE (Per testare voti giudice e proclama vincitore)
        if (!hackathonRepo.existsByNome("Hack Valutazione")) {
            Hackathon hValutazione = new HackathonBuilder()
                    .assegnaNome("Hack Valutazione")
                    .assegnaRegolamento("Regolamento hackathon concluso")
                    .assegnaScadenza(LocalDate.now().minusDays(15))
                    .assegnaDataInizio(LocalDate.now().minusDays(10))
                    .assegnaDataFine(LocalDate.now().minusDays(2)) // Finito da 2 giorni
                    .assegnaLuogo("Online")
                    .assegnaOrganizzatore(org).assegnaGiudice(giudice).assegnaMentore(mentore)
                    .assegnaDimMaxTeam(5).assegnaPremioImporto(new BigDecimal("3000")).build();

            hValutazione.setStato(new StatoInIscrizione());
            hValutazione.aggiungiTeam(teamA);
            hValutazione.setStato(new StatoInCorso());

            // Aggiungiamo una sottomissione pronta per essere valutata dal giudice
            Sottomissione sottomissione = new Sottomissione("Progetto_TeamAlpha.zip", "https://github.com/teama/progetto", teamA);
            hValutazione.aggiungiSottomissione(sottomissione);

            hValutazione.setStato(new StatoInValutazione()); // Forziamo lo stato in valutazione
            hackathonRepo.save(hValutazione);
        }

        // 6. CASO D'USO: INVITO PENDENTE
        if (invitoRepo.findByInvitatoAndStato(utenteSolo, new StatoPendente()).isEmpty()) {
            Invito invito = new Invito(utenteSolo, teamA);
            invitoRepo.save(invito);
        }

        // 7. CASO D'USO: RICHIESTA DI SUPPORTO (Da Team Alpha a Mentore)
        if (richiestaRepo.findAll().isEmpty() && hInCorso != null) {
            // La descrizione deve essere > 20 caratteri per superare la validazione
            RichiestaSupporto richiesta = new RichiestaSupporto(teamA, hInCorso, "Abbiamo un problema critico con il server di test e ci servirebbe aiuto per il deploy.");
            richiestaRepo.save(richiesta);
        }

        // 8. CASO D'USO: SEGNALAZIONE VIOLAZIONE (Da Mentore verso Team Beta)
        if (segnalazioneRepo.findAll().isEmpty() && hInCorso != null) {
            SegnalazioneViolazione segnalazione = new SegnalazioneViolazione(mentore, teamB, hInCorso, "Il team sta palesemente usando API a pagamento non autorizzate dal regolamento ufficiale.");
            segnalazioneRepo.save(segnalazione);
        }

        System.out.println("Dati pronti");
    }

    // --- Helper Methods ---
    private Utente creaUtente(String username, String email, String password) {
        return utenteRepo.findByUsername(username)
                .orElseGet(() -> utenteRepo.save(new Utente(username, email, password)));
    }

    private void aggiungiMembro(Team team, Utente u) {
        if (!team.isMembro(u)) {
            team.aggiungiMembro(u);
            utenteRepo.save(u); // Aggiorna la relazione nel DB
        }
    }
}