package it.unicam.hackhub.presentation.api;
import it.unicam.hackhub.application.controller.ValutareSottomissioneHandler;
import it.unicam.hackhub.domain.model.hackathon.Hackathon;
import it.unicam.hackhub.presentation.dto.HackathonResponse;
import it.unicam.hackhub.presentation.dto.SottomissioneResponse;
import it.unicam.hackhub.presentation.dto.ValutazioneRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/valutazioni")
public class ValutaSottomissioneController {

    private final ValutareSottomissioneHandler handler;

    public ValutaSottomissioneController(ValutareSottomissioneHandler handler) {
        this.handler = handler;
    }
    @GetMapping("/hackathons")
    public ResponseEntity<Set<HackathonResponse>> getHackathonsGiudice() {
        Set<HackathonResponse> response = handler.getHackathonsGiudice().stream()
                .map(h -> new HackathonResponse(h.getId(), h.getNome()))
                .collect(Collectors.toSet());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/sottomissioni/{nomeHackathon}")
    // 2. Usiamo @PathVariable invece di @RequestBody
    public ResponseEntity<Set<SottomissioneResponse>> getSottomissioni(@PathVariable String nomeHackathon) {
        Set<SottomissioneResponse> response = handler.getSottomissioni(nomeHackathon).stream()
                .map(s -> new SottomissioneResponse(
                        s.getId(),
                        s.getNomeFile(),
                        s.getLink(),
                        s.getDataCaricamento(),
                        s.getTeam().getNome(),
                        s.getPunteggio()
                ))
                .collect(Collectors.toSet());

        return ResponseEntity.ok(response);
    }
    @PostMapping
    public ResponseEntity<String> assegnaPunteggio(@RequestBody ValutazioneRequest request) {
        handler.assegnaPunteggio(request.nomeHackathon(), request.idSottomissione(), request.punteggio());
        return ResponseEntity.ok("Valutazione salvata con successo.");
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(403).body(ex.getMessage());
    }
}