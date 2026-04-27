package it.unicam.hackhub.presentation.api;

import it.unicam.hackhub.application.controller.CreazioneTeamHandler;
import it.unicam.hackhub.presentation.dto.CreazioneTeamRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams") // L'URL base per la gestione dei team
public class TeamController {

    private final CreazioneTeamHandler teamHandler;

    public TeamController(CreazioneTeamHandler teamHandler) {
        this.teamHandler = teamHandler;
    }

    @PostMapping("/creazione")
    public ResponseEntity<String> creaTeam(@RequestBody CreazioneTeamRequest request) {
        try {
            // NOTA: Controlla come si chiama il metodo esatto nel tuo CreazioneTeamHandler.
            // Potrebbe essere "creaTeam", "inizializzaTeam", o simile.
            // Aggiorna questa riga di conseguenza!
            teamHandler.creaTeam(request.nome(), request.datiBancari());

            return ResponseEntity.ok("Il team '" + request.nome() + "' è stato creato con successo!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            // Intercetta errori come "Devi essere loggato" o "Nome team già in uso"
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}