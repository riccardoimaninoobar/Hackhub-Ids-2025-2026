package it.unicam.hackhub.presentation.api;

import it.unicam.hackhub.application.controller.RegistrazioneHandler;
import it.unicam.hackhub.presentation.dto.RegistrazioneRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Indica a Spring che questa classe gestirà richieste web
@RequestMapping("/api/utenti") // L'URL base per questo controller
public class RegistrazioneController {

    private final RegistrazioneHandler registrazioneHandler;

    public RegistrazioneController(RegistrazioneHandler registrazioneHandler) {
        this.registrazioneHandler = registrazioneHandler;
    }

    @PostMapping("/registrazione") // Mappa le richieste POST all'URL /api/utenti/registrazione
    public ResponseEntity<String> registraUtente(@RequestBody RegistrazioneRequest request) {
        try {
            // Chiama il tuo handler passandogli i dati ricevuti
            registrazioneHandler.elaboraRegistrazione(
                    request.username(),
                    request.email(),
                    request.password()
            );
            return ResponseEntity.ok("Registrazione avvenuta con successo!");
        } catch (IllegalArgumentException e) {
            // Se la validazione fallisce nel tuo handler, restituisce un errore 400 (Bad Request)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}