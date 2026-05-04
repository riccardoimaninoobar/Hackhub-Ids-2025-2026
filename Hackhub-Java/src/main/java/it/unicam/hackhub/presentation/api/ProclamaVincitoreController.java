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

    @GetMapping("/valutazioni/{nomeHackathon}")
    public ResponseEntity<?> visualizzaValutazioni(@PathVariable String nomeHackathon) {
        Utente utenteLoggato = sessioneApp.getUtenteCorrente();
        if (utenteLoggato == null) {
            return ResponseEntity.status(401).body("Errore: Devi essere loggato per visualizzare le valutazioni.");
        }

        try {
            List<String> valutazioni = handler.getValutazioniTeam(nomeHackathon);
            return ResponseEntity.ok(valutazioni);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body("Accesso Negato: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Richiesta non valida: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Errore interno: " + e.getMessage());
        }
    }

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

        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body("Accesso Negato: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Richiesta non valida: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Errore interno: " + e.getMessage());
        }
    }
}