package it.unicam.hackhub.presentation.api;

import it.unicam.hackhub.application.controller.LoginHandler;
import it.unicam.hackhub.application.controller.LogoutHandler;
import it.unicam.hackhub.presentation.dto.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/utenti") // Raggruppiamo le API sugli utenti sotto lo stesso URL base
public class AutenticazioneController {

    private final LoginHandler loginHandler;
    private final LogoutHandler logoutHandler;

    public AutenticazioneController(LoginHandler loginHandler,  LogoutHandler logoutHandler) {
        this.loginHandler = loginHandler;
        this.logoutHandler = logoutHandler;
    }

    @PostMapping("/login")
    public ResponseEntity<String> effettuaLogin(@RequestBody LoginRequest request) {
        try {
            // NOTA BENE: Controlla come si chiama esattamente il metodo nel tuo LoginHandler.
            // Potrebbe essere "effettuaLogin", "elaboraLogin", o semplicemente "login".
            loginHandler.elaboraLogin(request.username(), request.password());

            return ResponseEntity.ok("Login effettuato con successo! Bentornato, " + request.username() + ".");
        } catch (IllegalArgumentException e) {
            // Se le credenziali sono errate, restituiamo un errore 400 (Bad Request)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/logout") // Mappa la richiesta POST su /api/utenti/logout
    public ResponseEntity<String> effettuaLogout() {
        try {
            // Chiama il metodo del tuo handler
            logoutHandler.effettuaLogout();
            return ResponseEntity.ok("Logout effettuato con successo. A presto!");
        } catch (IllegalStateException e) {
            // Se ad esempio non c'era nessuno loggato
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}