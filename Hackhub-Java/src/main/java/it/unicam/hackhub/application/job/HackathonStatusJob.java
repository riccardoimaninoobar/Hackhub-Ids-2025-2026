package it.unicam.hackhub.application.job;

import it.unicam.hackhub.domain.model.hackathon.Hackathon;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class HackathonStatusJob {

    private final HackathonRepository hackathonRepository;

    public HackathonStatusJob(HackathonRepository hackathonRepository) {
        this.hackathonRepository = hackathonRepository;
    }

    /**
     * Questo metodo viene eseguito in automatico ogni giorno a mezzanotte.
     * cron = "Secondi Minuti Ore Giorno Mese GiornoSettimana"
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void aggiornaStatiQuotidiano() {
        System.out.println("Avvio Job Notturno: Sincronizzazione stati Hackathon...");

        List<Hackathon> hackathons = hackathonRepository.findAll();

        for (Hackathon h : hackathons) {
            h.aggiornaStato(); // L'entità calcola se deve passare allo stato successivo
        }

        // Salviamo tutto. Grazie al "Dirty Checking" di Hibernate,
        // le query di UPDATE sul database verranno lanciate SOLO per
        // gli hackathon il cui stato è effettivamente cambiato!
        hackathonRepository.saveAll(hackathons);

        System.out.println("Job Notturno completato con successo.");
    }
}