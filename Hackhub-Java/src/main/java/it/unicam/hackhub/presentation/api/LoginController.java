package it.unicam.hackhub.presentation.api;

import it.unicam.hackhub.application.controller.LoginHandler;
import it.unicam.hackhub.presentation.dto.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/utenti") // Raggruppiamo le API sugli utenti sotto lo stesso URL base
public class LoginController {

    private final LoginHandler loginHandler;

    public LoginController(LoginHandler loginHandler) {
        this.loginHandler = loginHandler;
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
}