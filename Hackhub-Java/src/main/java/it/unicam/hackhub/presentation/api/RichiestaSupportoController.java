package it.unicam.hackhub.presentation.api;

import it.unicam.hackhub.application.controller.RichiestaSupportoHandler;
import it.unicam.hackhub.domain.model.hackathon.Hackathon;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.presentation.dto.HackathonResponse;
import it.unicam.hackhub.presentation.dto.InviaRichiestaSupportoRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supporto")
public class RichiestaSupportoController {

    private final RichiestaSupportoHandler handler;

    public RichiestaSupportoController(RichiestaSupportoHandler handler) {
        this.handler = handler;
    }

    @GetMapping("/hackathons")
    public ResponseEntity<?> ottieniHackathonDisponibili() {
        try {
            List<HackathonResponse> response = handler.getHackathons().stream()
                    .map(h -> new HackathonResponse(h.getId(), h.getNome()))
                    .toList();
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body("Azione non consentita: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<String> inviaRichiesta(@RequestBody InviaRichiestaSupportoRequest request) {
        try {
            handler.registraRichiestaSupporto(request.hackathonId(), request.descrizione());
            return ResponseEntity.ok("Richiesta di supporto inviata correttamente!");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body("Azione non consentita: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Dati non validi: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Errore durante l'invio della richiesta.");
        }
    }
}