package it.unicam.hackhub.presentation.api;

import it.unicam.hackhub.application.controller.SegnalaViolazioneHandler;
import it.unicam.hackhub.presentation.dto.HackathonSupportoResponse; // Riusiamo questo DTO per comodità
import it.unicam.hackhub.presentation.dto.TeamResponse;
import it.unicam.hackhub.presentation.dto.SegnalazioneRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/violazioni")
public class ViolazioneController {

    @Autowired
    private SegnalaViolazioneHandler segnalazioneHandler;

    // Freccia 1: getHackathonsAssegnati()
    @GetMapping("/hackathons-assegnati")
    public ResponseEntity<List<HackathonSupportoResponse>> getHackathonsAssegnati() {
        List<HackathonSupportoResponse> hackathons = segnalazioneHandler.getHackathonsAssegnati();
        return ResponseEntity.ok(hackathons);
    }

    // Freccia 2: getTeamPartecipanti(hackathonId)
    @GetMapping("/hackathons/{hackathonId}/teams")
    public ResponseEntity<List<TeamResponse>> getTeamPartecipanti(@PathVariable Long hackathonId) {
        List<TeamResponse> teams = segnalazioneHandler.getTeamPartecipanti(hackathonId);
        return ResponseEntity.ok(teams);
    }

    // Freccia 3: inserisciSegnalazione(request)
    @PostMapping
    public ResponseEntity<String> inserisciSegnalazione(@RequestBody SegnalazioneRequest request) {
        segnalazioneHandler.inserisciSegnalazione(request);
        return ResponseEntity.ok("Segnalazione inserita correttamente");
    }
}