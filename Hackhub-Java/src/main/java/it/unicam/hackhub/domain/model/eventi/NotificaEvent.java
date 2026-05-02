package it.unicam.hackhub.domain.model.eventi;

import it.unicam.hackhub.domain.model.Utente;

public record NotificaEvent(Utente destinatario, String titolo, String messaggio) {}