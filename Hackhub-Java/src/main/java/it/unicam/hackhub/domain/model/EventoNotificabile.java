package it.unicam.hackhub.domain.model;

public interface EventoNotificabile {
    Utente getDestinatarioNotifica();
    String getTestoNotifica();
}