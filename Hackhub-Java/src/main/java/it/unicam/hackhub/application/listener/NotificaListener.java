package it.unicam.hackhub.application.listener;

import it.unicam.hackhub.domain.model.EventoNotificabile;
import it.unicam.hackhub.domain.model.Notifica;
import it.unicam.hackhub.domain.repository.NotificaRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NotificaListener {

    private final NotificaRepository notificaRepo;

    public NotificaListener(NotificaRepository notificaRepo) {
        this.notificaRepo = notificaRepo;
    }

    @EventListener
    public void handleNotificaGenerica(EventoNotificabile event) {
        // Estrae dinamicamente il testo e il destinatario grazie al Polimorfismo
        Notifica notifica = new Notifica(
                event.getDestinatarioNotifica(),
                "Nuova Notifica", // Titolo fisso, o puoi aggiungerlo all'interfaccia EventoNotificabile
                event.getTestoNotifica()
        );

        notificaRepo.save(notifica);
    }
}