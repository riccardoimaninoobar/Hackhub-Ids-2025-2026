package it.unicam.hackhub.presentation.api;

import it.unicam.hackhub.application.controller.AccettazioneInvitoHandler;
import it.unicam.hackhub.application.controller.GestioneInvitiHandler;
import it.unicam.hackhub.domain.model.Invito;
import it.unicam.hackhub.presentation.dto.InvioInvitoRequest;
import it.unicam.hackhub.presentation.dto.InvitoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inviti") // URL base per la gestione degli inviti
public class InvitoController {

    private final GestioneInvitiHandler gestioneInvitiHandler;

    private final AccettazioneInvitoHandler accettazioneHandler;

    public InvitoController(GestioneInvitiHandler gestioneInvitiHandler,
                            AccettazioneInvitoHandler accettazioneHandler) {
        this.gestioneInvitiHandler = gestioneInvitiHandler;
        this.accettazioneHandler = accettazioneHandler;
    }

    @PostMapping("/invia")
    public ResponseEntity<String> inviaInvito(@RequestBody InvioInvitoRequest request) {
        try {
            // L'handler si occuperà di verificare se l'utente loggato ha un team
            // e se l'utente invitato esiste ed è idoneo
            gestioneInvitiHandler.elaboraInvito(request.username());

            return ResponseEntity.ok("Invito inviato con successo all'utente '" + request.username() + "'!");

        } catch (IllegalStateException | IllegalArgumentException e) {
            // Cattura le eccezioni sollevate dal tuo handler (es. "L'utente è già in un team")
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Errore interno del server: " + e.getMessage());
        }
    }
    // 1. Endpoint per vedere gli inviti in attesa
    @GetMapping("/pendenti")
    public ResponseEntity<?> visualizzaInvitiPendenti() {
        try {
            List<Invito> pendenti = accettazioneHandler.getInvitiPendenti();

            if (pendenti.isEmpty()) {
                return ResponseEntity.ok("Non hai nessun invito in sospeso.");
            }

            // Mappa le entità nel DTO da restituire in formato JSON
            List<InvitoResponse> response = pendenti.stream()
                    .map(i -> new InvitoResponse(i.getId(), i.getTeamMittente().getNome()))
                    .toList();

            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            // Cattura l'errore se l'utente non è loggato
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Errore interno: " + e.getMessage());
        }
    }

    // 2. Endpoint per accettare un invito specifico usando il suo ID
    @PostMapping("/{id}/accetta")
    public ResponseEntity<String> accettaInvito(@PathVariable Long id) {
        try {
            // Recupera gli inviti pendenti per l'utente loggato
            List<Invito> pendenti = accettazioneHandler.getInvitiPendenti();

            // Cerca l'invito con l'ID fornito nella lista di quelli pendenti
            Invito invitoDaAccettare = pendenti.stream()
                    .filter(i -> i.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invito non trovato o non appartiene a te."));

            // Passa l'oggetto Invito trovato all'handler
            accettazioneHandler.accettaInvito(invitoDaAccettare);

            return ResponseEntity.ok("Hai accettato l'invito! Ora fai parte del team '" +
                    invitoDaAccettare.getTeamMittente().getNome() + "'.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Errore interno: " + e.getMessage());
        }
    }
}