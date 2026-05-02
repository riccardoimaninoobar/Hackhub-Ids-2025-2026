package it.unicam.hackhub.application.listener;

import it.unicam.hackhub.domain.model.Notifica;
import it.unicam.hackhub.domain.model.eventi.NotificaEvent;
import it.unicam.hackhub.domain.repository.NotificaRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BachecaEventListener {

    private final NotificaRepository notificaRepository;

    public BachecaEventListener(NotificaRepository notificaRepository) {
        this.notificaRepository = notificaRepository;
    }

    public void handleNotificaEvent(NotificaEvent event) {
        Notifica notifica = new Notifica(event.destinatario(), event.titolo(), event.messaggio());
        notificaRepository.save(notifica);
    }
}