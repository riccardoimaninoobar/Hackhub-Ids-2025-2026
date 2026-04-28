package it.unicam.hackhub.presentation.api;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.application.controller.ProclamaVincitoreHandler;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.presentation.dto.ProclamaVincitoreRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vincitore")
public class ProclamaVincitoreController {

    private final ProclamaVincitoreHandler handler;
    private final Sessione sessioneApp;

    public ProclamaVincitoreController(ProclamaVincitoreHandler handler, Sessione sessioneApp) {
        this.handler = handler;
        this.sessioneApp = sessioneApp;
    }

    // 1. Endpoint per vedere i punteggi/valutazioni (corrisponde al passo 1 e 2 della CLI)
    // Usiamo @PathVariable per passare il nome dell'hackathon direttamente nell'URL
    @GetMapping("/valutazioni/{nomeHackathon}")
    public ResponseEntity<?> visualizzaValutazioni(@PathVariable String nomeHackathon) {
        Utente utenteLoggato = sessioneApp.getUtenteCorrente();
        if (utenteLoggato == null) {
            return ResponseEntity.status(401).body("Errore: Devi essere loggato per visualizzare le valutazioni.");
        }

        try {
            List<String> valutazioni = handler.getValutazioniTeam(nomeHackathon);
            return ResponseEntity.ok(valutazioni);
        } catch (IllegalStateException | IllegalArgumentException e) {
            // Gestisce hackathon non trovato o non in stato di valutazione
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Errore interno: " + e.getMessage());
        }
    }

    // 2. Endpoint per proclamare il vincitore (corrisponde ai passi da 3 a 8 della CLI)
    @PostMapping("/proclama")
    public ResponseEntity<String> proclamaVincitore(@RequestBody ProclamaVincitoreRequest request) {
        Utente utenteLoggato = sessioneApp.getUtenteCorrente();
        if (utenteLoggato == null) {
            return ResponseEntity.status(401).body("Errore: Devi essere loggato per proclamare un vincitore.");
        }

        try {
            boolean successo = handler.proclamaVincitore(request.nomeHackathon(), request.nomeTeam());

            if (successo) {
                return ResponseEntity.ok("Hackathon concluso con successo. Team '" + request.nomeTeam() + "' proclamato e premio erogato.");
            } else {
                return ResponseEntity.status(500).body("Errore nell'erogazione del premio. La proclamazione è stata annullata.");
            }

        } catch (IllegalStateException | IllegalArgumentException e) {
            // Gestisce errori logici (es. team inesistente, stato errato)
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Errore interno: " + e.getMessage());
        }
    }
}