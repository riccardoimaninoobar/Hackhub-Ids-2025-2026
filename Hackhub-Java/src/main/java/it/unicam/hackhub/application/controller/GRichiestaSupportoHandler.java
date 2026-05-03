package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.RichiestaSupporto;
import it.unicam.hackhub.domain.model.Team;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.model.eventi.NotificaEvent;
import it.unicam.hackhub.domain.model.hackathon.Hackathon;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.RichiestaSupportoRepository;
import it.unicam.hackhub.domain.service.CalendarService;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GRichiestaSupportoHandler {
    private final Sessione sessione;
    private final RichiestaSupportoRepository richiestaRepo;
    private final HackathonRepository hackathonRepo;
    private final CalendarService calendarService;
    private final ApplicationEventPublisher eventPublisher;

    public GRichiestaSupportoHandler(Sessione sessione,
                                     RichiestaSupportoRepository richiestaRepo,
                                     HackathonRepository hackathonRepo,
                                     CalendarService calendarService,
                                     ApplicationEventPublisher eventPublisher) {
        this.sessione = sessione;
        this.richiestaRepo = richiestaRepo;
        this.hackathonRepo = hackathonRepo;
        this.calendarService = calendarService;
        this.eventPublisher = eventPublisher;
    }

    public List<RichiestaSupporto> getRichiesteSupporto() {
        Utente corrente = getMentoreAutenticato();
        List<RichiestaSupporto> richieste = richiestaRepo.findAll().stream()
                .filter(r -> r.isAperta() && r.getHackathon().isMentore(corrente))
                .collect(Collectors.toList());

        if (richieste.isEmpty()) {
            throw new IllegalStateException("Non ci sono richieste di supporto da gestire.");
        }
        return richieste;
    }

    public List<RichiestaSupporto> ottieniRichiestePerMentore(Utente mentore) {
        return richiestaRepo.findAll().stream()
                .filter(r -> r.getStato() != null &&
                        ("APERTA".equalsIgnoreCase(r.getStato().toString())
                                || "PENDENTE".equalsIgnoreCase(r.getStato().toString())
                                || "RISPOSTA_INSERITA".equalsIgnoreCase(r.getStato().toString())
                                || "RISPOSTAINSERITA".equalsIgnoreCase(r.getStato().toString())))
                .filter(r -> r.getHackathon().isMentore(mentore))
                .collect(Collectors.toList());
    }

    public void rispondiRichiesta(RichiestaSupporto richiesta, String risposta) {
        if (risposta == null || risposta.trim().isEmpty()) {
            throw new IllegalArgumentException("La risposta non può essere vuota.");
        }
        richiesta.aggiungiRisposta(risposta.trim());
        richiestaRepo.save(richiesta);
    }

    public void prenotaSlotCalendar(RichiestaSupporto richiesta, LocalDate data, LocalTime ora) {
        if (!calendarService.verificaDisponibilita(data, ora)) {
            throw new IllegalArgumentException("Slot non disponibile.");
        }
        boolean prenotazioneConfermata = calendarService.prenotaSlot(data, ora);
        if (!prenotazioneConfermata) {
            throw new IllegalStateException("Errore durante la prenotazione dello slot.");
        }
        richiesta.associaSlot(data, ora);
        richiestaRepo.save(richiesta);
    }

    public void gestisciRichiesta(RichiestaSupporto richiesta, String risposta, LocalDate data, LocalTime ora) {
        Utente mentore = getMentoreAutenticato();
        if (richiesta == null) {
            throw new IllegalArgumentException("Richiesta di supporto non valida.");
        }
        Hackathon hackathon = richiesta.getHackathon();
        if (hackathon == null || !hackathon.isMentore(mentore)) {
            throw new IllegalStateException("Non sei autorizzato a gestire questa richiesta di supporto.");
        }
        rispondiRichiesta(richiesta, risposta);
        prenotaSlotCalendar(richiesta, data, ora);
        pubblicaNotifichePerBacheca(richiesta, risposta, data, ora);
    }

    private void pubblicaNotifichePerBacheca(RichiestaSupporto richiesta, String risposta, LocalDate data, LocalTime ora) {
        Team team = richiesta.getTeam();
        if (team == null || team.getMembers() == null || team.getMembers().isEmpty()) {
            return;
        }
        String titolo = "Richiesta di supporto gestita";
        String messaggio = "La tua richiesta di supporto per l'hackathon "
                + richiesta.getHackathon().getNome()
                + " è stata gestita dal mentore. "
                + "Risposta: " + risposta.trim()
                + ". Slot prenotato per il "
                + data + " alle " + ora + ".";
        for (Utente membro : team.getMembers()) {
            eventPublisher.publishEvent(new NotificaEvent(membro, titolo, messaggio));
        }
    }

    private Utente getMentoreAutenticato() {
        Utente corrente = sessione.getUtenteCorrente();
        if (corrente == null) {
            throw new IllegalStateException("Devi effettuare il login.");
        }
        List<Hackathon> hackathons = hackathonRepo.findAll().stream()
                .filter(h -> h.isMentore(corrente))
                .collect(Collectors.toList());
        if (hackathons.isEmpty()) {
            throw new IllegalStateException("Non sei autorizzato a gestire richieste di supporto.");
        }
        return corrente;
    }
}
