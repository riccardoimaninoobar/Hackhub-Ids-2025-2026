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
    private final HackathonRepository hackathonRepo;

    public RichiestaSupportoController(RichiestaSupportoHandler handler, HackathonRepository hackathonRepo) {
        this.handler = handler;
        this.hackathonRepo = hackathonRepo;
    }

    // Endpoint per la selezione iniziale
    @GetMapping("/hackathons")
    public ResponseEntity<?> ottieniHackathonDisponibili() {
        try {
            List<HackathonResponse> response = handler.getHackathons().stream()
                    .map(h -> new HackathonResponse(h.getId(), h.getNome()))
                    .toList();
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint per l'invio della richiesta
    @PostMapping
    public ResponseEntity<String> inviaRichiesta(@RequestBody InviaRichiestaSupportoRequest request) {
        try {
            // Conversione ID -> Oggetto Hackathon
            Hackathon h = hackathonRepo.findById(request.hackathonId())
                    .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato."));

            // Validazione e registrazione
            handler.registraRichiestaSupporto(h, request.descrizione());

            return ResponseEntity.ok("Richiesta di supporto inviata correttamente!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Gestisce descrizione breve, mancanza login o hackathon errato
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Errore durante l'invio della richiesta.");
        }
    }
}