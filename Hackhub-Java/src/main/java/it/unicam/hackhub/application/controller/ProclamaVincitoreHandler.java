package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.domain.model.Hackathon;
import it.unicam.hackhub.domain.model.Sottomissione;
import it.unicam.hackhub.domain.model.StatoConcluso;
import it.unicam.hackhub.domain.model.StatoInValutazione; // Aggiunto import
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.TeamRepository;
import it.unicam.hackhub.domain.service.SistemaPagamentoAdapter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProclamaVincitoreHandler {

    private final HackathonRepository hackathonRepo;
    private final TeamRepository teamRepo;
    private final SistemaPagamentoAdapter sistemaPagamento;

    public ProclamaVincitoreHandler(HackathonRepository hackathonRepo,
                                    TeamRepository teamRepo,
                                    SistemaPagamentoAdapter sistemaPagamento) {
        this.hackathonRepo = hackathonRepo;
        this.teamRepo = teamRepo;
        this.sistemaPagamento = sistemaPagamento;
    }

    public List<String> getValutazioniTeam(String nomeHackathon) {
        Optional<Hackathon> optHackathon = hackathonRepo.findByNome(nomeHackathon);
        if (optHackathon.isEmpty()) {
            throw new IllegalArgumentException("Hackathon non trovato.");
        }
        Hackathon hackathon = optHackathon.get();

        // CORREZIONE: Controllo del tipo tramite instanceof
        if (!(hackathon.getStato() instanceof StatoInValutazione)) {
            // Usa getClass().getSimpleName() per stampare in modo leggibile il nome della classe dello stato attuale
            throw new IllegalStateException("L'Hackathon non è in stato di valutazione. Stato attuale: " +
                    (hackathon.getStato() != null ? hackathon.getStato().getClass().getSimpleName() : "Nullo"));
        }

        List<String> valutazioni = new ArrayList<>();
        for (Sottomissione s : hackathon.getSottomissioni()) {
            String nomeTeam = s.getTeam() != null ? s.getTeam().getName() : "Team Sconosciuto";
            valutazioni.add("Team: " + nomeTeam + " -> Punteggio: " + s.getPunteggio());
        }
        return valutazioni;
    }

    public boolean proclamaVincitore(String nomeHackathon, String nomeTeam) {
        Hackathon hackathon = hackathonRepo.findByNome(nomeHackathon)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato."));
        Team team = teamRepo.findByNome(nomeTeam)
                .orElseThrow(() -> new IllegalArgumentException("Team non trovato."));

        if (!(hackathon.getStato() instanceof StatoInValutazione)) {
            throw new IllegalStateException("Impossibile proclamare il vincitore: l'Hackathon non è in stato 'In valutazione'.");
        }

        if (!sistemaPagamento.erogaPremio(hackathon.getPremioInDenaro(), team.getDatiBancari())) {
            System.err.println("Fallimento: impossibile erogare il premio. L'Hackathon permane in valutazione.");
            return false;
        }

        hackathon.setTeamVincente(team);
        hackathon.setStato(new StatoConcluso());
        hackathonRepo.save(hackathon);
        System.out.println("NOTIFICA: Complimenti ai membri del team " + team.getName() + ", avete vinto!");
        return true;
    }
}