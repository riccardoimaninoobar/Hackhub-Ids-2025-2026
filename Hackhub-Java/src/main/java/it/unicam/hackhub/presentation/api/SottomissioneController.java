package it.unicam.hackhub.presentation.api;

import it.unicam.hackhub.application.controller.CaricaSottomissioneHandler;
import it.unicam.hackhub.domain.model.Hackathon;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.presentation.dto.HackathonDisponibileResponse;
import it.unicam.hackhub.presentation.dto.SottomissioneRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sottomissioni")
public class SottomissioneController {

    private final CaricaSottomissioneHandler handler;
    private final HackathonRepository hackathonRepo; // Necessario per convertire ID -> Oggetto

    public SottomissioneController(CaricaSottomissioneHandler handler, HackathonRepository hackathonRepo) {
        this.handler = handler;
        this.hackathonRepo = hackathonRepo;
    }

    // Corrisponde alla prima parte della CLI: recupera gli hackathon "In corso" per il team
    @GetMapping("/hackathons-disponibili")
    public ResponseEntity<?> getHackathonsDisponibili() {
        try {
            Set<Hackathon> hs = handler.getHackathonInCorso();

            if (hs.isEmpty()) {
                return ResponseEntity.ok("Nessun Hackathon in corso disponibile per il caricamento.");
            }

            List<HackathonDisponibileResponse> response = hs.stream()
                    .map(h -> new HackathonDisponibileResponse(h.getId(), h.getNome()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    // Corrisponde alla seconda parte della CLI: esegue il caricamento
    @PostMapping
    public ResponseEntity<String> caricaSottomissione(@RequestBody SottomissioneRequest request) {
        try {
            // Recuperiamo l'oggetto Hackathon dal database usando l'ID del DTO
            Hackathon hackathon = hackathonRepo.findById(request.hackathonId())
                    .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato con ID: " + request.hackathonId()));

            // Chiamata all'handler con l'oggetto recuperato, proprio come faceva la CLI
            handler.caricaSottomissione(hackathon, request.link());

            return ResponseEntity.ok("Sottomissione per l'hackathon '" + hackathon.getNome() + "' caricata correttamente!");

        } catch (IllegalStateException | IllegalArgumentException e) {
            // Gestisce errori di login, di team o di stato dell'hackathon
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Errore interno: " + e.getMessage());
        }
    }
}