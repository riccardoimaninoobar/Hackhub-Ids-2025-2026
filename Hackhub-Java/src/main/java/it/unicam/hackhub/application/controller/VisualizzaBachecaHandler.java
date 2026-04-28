package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.domain.model.Notifica;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.NotificaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisualizzaBachecaHandler {
    private final NotificaRepository notificaRepository;

    public VisualizzaBachecaHandler(NotificaRepository notificaRepository) {
        this.notificaRepository = notificaRepository;
    }

    public List<Notifica> ottieniNotifiche(Utente utente) {
        return notificaRepository.findByDestinatario(utente);
    }
}