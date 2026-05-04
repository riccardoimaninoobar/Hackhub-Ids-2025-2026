package it.unicam.hackhub.presentation.api;

import it.unicam.hackhub.application.controller.GestisciViolazioneHandler;
import it.unicam.hackhub.application.controller.SegnalaViolazioneHandler;
import it.unicam.hackhub.application.controller.VisualizzaViolazioniHandler;
import it.unicam.hackhub.presentation.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/violazioni")
public class ViolazioneController {

    @Autowired
    private SegnalaViolazioneHandler segnalazioneHandler;
    @Autowired
    private VisualizzaViolazioniHandler visualizzaHandler;
    @Autowired
    private GestisciViolazioneHandler gestisciHandler;

    @GetMapping("/hackathons-assegnati")
    public ResponseEntity<List<HackathonResponse>> getHackathonsAssegnati() {
        List<HackathonResponse> hackathons = segnalazioneHandler.getHackathonsAssegnati();
        return ResponseEntity.ok(hackathons);
    }

    @GetMapping("/hackathons/{hackathonId}/teams")
    public ResponseEntity<List<TeamResponse>> getTeamPartecipanti(@PathVariable Long hackathonId) {
        List<TeamResponse> teams = segnalazioneHandler.getTeamPartecipanti(hackathonId);
        return ResponseEntity.ok(teams);
    }

    @PostMapping
    public ResponseEntity<String> inserisciSegnalazione(@RequestBody SegnalazioneRequest request) {
        segnalazioneHandler.inserisciSegnalazione(request);
        return ResponseEntity.ok("Segnalazione inserita correttamente");
    }

    @GetMapping
    public ResponseEntity<List<SegnalazioneResponse>> getViolazioni() {
        List<SegnalazioneResponse> violazioni = visualizzaHandler.getViolazioni();
        return ResponseEntity.ok(violazioni);
    }

    @PostMapping("/{violazioneId}/gestisci")
    public ResponseEntity<String> inserisciProvvedimento(
            @PathVariable Long violazioneId,
            @RequestBody GestioneViolazioneRequest request) {

        gestisciHandler.gestisciViolazione(violazioneId, request.esito(), request.motivazione());
        return ResponseEntity.ok("Violazione gestita correttamente");
    }

    // --- GESTIONE GLOBALE DELLE ECCEZIONI PER QUESTO CONTROLLER ---

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity.status(403).body("Azione non consentita: " + e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body("Dati non validi: " + e.getMessage());
    }
}