package it.unicam.hackhub.presentation.api;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.application.controller.VisualizzaBachecaHandler;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.presentation.dto.NotificaDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bacheca")
public class BachecaController {
    private final VisualizzaBachecaHandler bachecaHandler;
    private final Sessione sessione;

    public BachecaController(VisualizzaBachecaHandler bachecaHandler, Sessione sessione) {
        this.bachecaHandler = bachecaHandler;
        this.sessione = sessione;
    }

    @GetMapping
    public ResponseEntity<?> ottieniBachecaPersonale() {
        Utente corrente = sessione.getUtenteCorrente();
        if (corrente == null) return ResponseEntity.status(401).body("Devi effettuare il login per visualizzare la bacheca.");

        List<NotificaDTO> notifiche = bachecaHandler.ottieniNotifiche(corrente).stream()
                .map(n -> new NotificaDTO(n.getTitolo(), n.getMessaggio()))
                .toList();

        return ResponseEntity.ok(notifiche);
    }
}