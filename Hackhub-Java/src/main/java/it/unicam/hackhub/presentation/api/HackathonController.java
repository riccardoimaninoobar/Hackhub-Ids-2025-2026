package it.unicam.hackhub.presentation.api;

import it.unicam.hackhub.application.controller.AggiungiMentoreHandler;
import it.unicam.hackhub.application.controller.CreazioneHackathonHandler;
import it.unicam.hackhub.application.controller.IscrizioneTeamHandler;
import it.unicam.hackhub.presentation.dto.AggiungiMentoreRequest;
import it.unicam.hackhub.presentation.dto.CreazioneHackathonRequest;
import it.unicam.hackhub.presentation.dto.HackathonResponse;
import it.unicam.hackhub.presentation.dto.IscrizioneTeamRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.unicam.hackhub.application.controller.ConsultareHackathonHandler;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hackathons") // URL base per tutto ciò che riguarda gli Hackathon
public class HackathonController {

    private final CreazioneHackathonHandler creazioneHandler;
    private final ConsultareHackathonHandler consultareHandler;
    private final AggiungiMentoreHandler aggiungiMentoreHandler;
    private final IscrizioneTeamHandler iscrizioneTeamHandler;

    public HackathonController(CreazioneHackathonHandler creazioneHandler,
                               ConsultareHackathonHandler consultareHandler,
                               AggiungiMentoreHandler aggiungiMentoreHandler,
                               IscrizioneTeamHandler  iscrizioneTeamHandler) {
        this.creazioneHandler = creazioneHandler;
        this.consultareHandler = consultareHandler;
        this.aggiungiMentoreHandler = aggiungiMentoreHandler;
        this.iscrizioneTeamHandler = iscrizioneTeamHandler;
    }

    @PostMapping("/creazione")
    public ResponseEntity<String> creaHackathon(@RequestBody CreazioneHackathonRequest request) {
        try {
            // 1. Crea l'impalcatura dell'Hackathon in memoria (il Builder)
            creazioneHandler.creaHackathonBase(
                    request.nome(),
                    request.regolamento(),
                    request.scadenza(),
                    request.inizio(),
                    request.fine(),
                    request.luogo(),
                    request.maxTeam(),
                    request.premio()
            );

            // 2. Assegna il giudice (Questo metodo nel tuo Handler richiama il .build() e salva nel database!)
            boolean giudiceAssegnato = creazioneHandler.assegnaGiudice(request.nomeGiudice());

            if (!giudiceAssegnato) {
                return ResponseEntity.badRequest().body("Creazione fallita: L'utente indicato come giudice non esiste.");
            }

            return ResponseEntity.ok("Hackathon '" + request.nome() + "' creato con successo!");

        } catch (IllegalStateException | IllegalArgumentException e) {
            // Cattura errori come "Devi effettuare il login" o "Hackathon già esistente"
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/consultazione")
    public ResponseEntity<List<HackathonResponse>> visualizzaTutti() {
        // Recuperiamo la lista grezza dal database tramite l'handler
        var listaHackathon = consultareHandler.getListaHackathon();

        // Trasformiamo ogni Hackathon in un HackathonResponse (DTO)
        List<HackathonResponse> response = listaHackathon.stream()
                .map(h -> new HackathonResponse(
                        h.getNome(),
                        h.getLuogo(),
                        h.getDataInizio(),
                        h.getDataFine(),
                        h.getScadenzaIscrizioni(),
                        h.getOrganizzatore().getUsername(),
                        h.getStato().toString()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/aggiungi-mentore")
    public ResponseEntity<String> aggiungiMentore(@RequestBody AggiungiMentoreRequest request) {
        try {
            // Uniamo le due chiamate necessarie all'handler
            aggiungiMentoreHandler.checkOrg(request.nomeHackathon());
            aggiungiMentoreHandler.aggiungiMentore(request.usernameMentore());

            return ResponseEntity.ok("Il mentore '" + request.usernameMentore() +
                    "' è stato aggiunto all'Hackathon '" + request.nomeHackathon() + "'.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Errore interno: " + e.getMessage());
        }
    }

    // ENDPOINT 1 per UC iscrizione team: Restituisce solo gli Hackathon attualmente in fase di iscrizione
    @GetMapping("/in-iscrizione")
    public ResponseEntity<List<HackathonResponse>> visualizzaIscrivibili() {
        var listaHackathon = iscrizioneTeamHandler.getHackathonInIscrizione();

        List<HackathonResponse> response = listaHackathon.stream()
                .map(h -> new HackathonResponse(
                        h.getNome(),
                        h.getLuogo(),
                        h.getDataInizio(),
                        h.getDataFine(),
                        h.getScadenzaIscrizioni(), h.getOrganizzatore().getUsername(),
                        h.getStato().toString()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ENDPOINT 2: Permette l'iscrizione del team loggato all'hackathon specificato
    @PostMapping("/iscrizione-team")
    public ResponseEntity<String> iscriviTeam(@RequestBody IscrizioneTeamRequest request) {
        try {
            iscrizioneTeamHandler.iscriviTeam(request.nomeHackathon());
            return ResponseEntity.ok("Il tuo team è stato iscritto all'Hackathon '" + request.nomeHackathon() + "' con successo!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Errore interno: " + e.getMessage());
        }
    }
}