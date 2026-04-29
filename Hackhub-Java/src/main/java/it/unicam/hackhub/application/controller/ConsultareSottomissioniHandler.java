package it.unicam.hackhub.application.controller;
import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Hackathon;
import it.unicam.hackhub.domain.model.Sottomissione;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.UtenteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@Service
@Transactional
public class ConsultareSottomissioniHandler {
    private final HackathonRepository hackathonRepository;
    private final UtenteRepository utenteRepository;
    private final Sessione sessione;
    public ConsultareSottomissioniHandler(HackathonRepository hackathonRepository,
                                          UtenteRepository utenteRepository,
                                          Sessione sessione) {
        this.hackathonRepository = hackathonRepository;
        this.utenteRepository = utenteRepository;
        this.sessione = sessione;
    }
    public List<Sottomissione> getSottomissioniHackathon(String nomeHackathon) {
        Utente utenteInSessione = sessione.getUtenteCorrente();
        if (utenteInSessione == null) {
            throw new IllegalStateException("Devi effettuare il login per consultare le sottomissioni.");
        }
        Utente utente = utenteRepository.findById(utenteInSessione.getId())
                .orElseThrow(() -> new IllegalStateException("Utente non più valido nel DB."));
        Hackathon hackathon = hackathonRepository.findByNome(nomeHackathon)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato."));
        if (!hackathon.utenteMembroStaff(utente)) {
            throw new IllegalStateException("Non sei autorizzato a consultare le sottomissioni di questo Hackathon.");
        }
        return hackathon.getSottomissioni().stream()
                .sorted(Comparator.comparing(Sottomissione::getDataCaricamento))
                .collect(Collectors.toList());
    }
}
