package it.unicam.hackhub.presentation.api;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.application.controller.GRichiestaSupportoHandler;
import it.unicam.hackhub.domain.model.RichiestaSupporto;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.RichiestaSupportoRepository;
import it.unicam.hackhub.presentation.dto.GestisciRichiestaRequest;
import it.unicam.hackhub.presentation.dto.RichiestaSupportoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentore/richieste")
public class MentoreRichiesteController {
    private final GRichiestaSupportoHandler handler;
    private final Sessione sessione;
    private final RichiestaSupportoRepository richiestaRepo; // <-- Aggiunto per recuperare l'ID

    public MentoreRichiesteController(GRichiestaSupportoHandler handler, Sessione sessione, RichiestaSupportoRepository richiestaRepo) {
        this.handler = handler;
        this.sessione = sessione;
        this.richiestaRepo = richiestaRepo;
    }

    // 1. Il Mentore visualizza le richieste (GIÀ FATTO DA TE)
    @GetMapping
    public ResponseEntity<?> ottieniRichiesteAssegnate() {
        try {
            Utente corrente = sessione.getUtenteCorrente();
            if (corrente == null) return ResponseEntity.status(401).body("Devi effettuare il login per procedere.");

            List<RichiestaSupportoDTO> dtos = handler.ottieniRichiestePerMentore(corrente).stream()
                    .map(r -> new RichiestaSupportoDTO(r.getId(), r.getTeam().getNome(), r.getDescrizione(), r.getStato().toString()))
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. Il Mentore risponde e fissa la call (MANCANTE)
    @PostMapping("/{id}/rispondi")
    public ResponseEntity<String> gestisciRichiesta(@PathVariable Long id, @RequestBody GestisciRichiestaRequest request) {
        try {
            Utente corrente = sessione.getUtenteCorrente();
            if (corrente == null) return ResponseEntity.status(401).body("Devi effettuare il login per procedere.");

            // Recupera la richiesta dal DB
            RichiestaSupporto richiesta = richiestaRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Richiesta di supporto non trovata."));

            // Delega la logica di business all'handler
            handler.gestisciRichiesta(richiesta, request.risposta(), request.data(), request.ora());

            return ResponseEntity.ok("Richiesta gestita con successo. Slot prenotato per il " + request.data() + " alle " + request.ora());

        } catch (IllegalArgumentException | IllegalStateException e) {
            // Gestisce slot non disponibile, campi vuoti o errori di stato
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Errore interno: " + e.getMessage());
        }
    }
}