package it.unicam.hackhub.presentation.api;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.application.controller.GRichiestaSupportoHandler;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.presentation.dto.RichiestaSupportoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mentore/richieste")
public class MentoreRichiesteController {
    private final GRichiestaSupportoHandler handler;
    private final Sessione sessione;

    public MentoreRichiesteController(GRichiestaSupportoHandler handler, Sessione sessione) {
        this.handler = handler;
        this.sessione = sessione;
    }

    @GetMapping
    public ResponseEntity<?> ottieniRichiesteAssegnate() {
        try {
            Utente corrente = sessione.getUtenteCorrente();
            if (corrente == null) return ResponseEntity.status(401).body("Devi effettuare il login per procedere.");

            List<RichiestaSupportoDTO> dtos = handler.ottieniRichiestePerMentore(corrente).stream()
                    .map(r -> new RichiestaSupportoDTO(r.getId(), r.getTeam().getNome(), r.getMessaggio(), r.getStato().toString()))
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}