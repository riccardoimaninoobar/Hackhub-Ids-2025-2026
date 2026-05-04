package it.unicam.hackhub.presentation.api;

import it.unicam.hackhub.application.controller.CaricaSottomissioneHandler;
import it.unicam.hackhub.domain.model.hackathon.Hackathon;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.presentation.dto.HackathonResponse;
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

    public SottomissioneController(CaricaSottomissioneHandler handler) {
        this.handler = handler;
    }

    // Corrisponde alla prima parte della CLI: recupera gli hackathon "In corso" per il team
    @GetMapping("/hackathons-disponibili")
    public ResponseEntity<?> getHackathonsDisponibili() {
        try {
            Set<Hackathon> hs = handler.getHackathonInCorso();

            if (hs.isEmpty()) {
                return ResponseEntity.ok("Nessun Hackathon in corso disponibile per il caricamento.");
            }

            List<HackathonResponse> response = hs.stream()
                    .map(h -> new HackathonResponse(h.getId(), h.getNome()))
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
            // Passa semplicemente l'ID all'handler
            handler.caricaSottomissione(request.hackathonId(), request.link());
            return ResponseEntity.ok("Sottomissione caricata correttamente!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Errore interno: " + e.getMessage());
        }
    }
}