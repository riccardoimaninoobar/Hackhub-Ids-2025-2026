package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.application.context.Sessione;
import it.unicam.hackhub.domain.model.Hackathon;
import it.unicam.hackhub.domain.model.RichiestaSupporto;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.domain.repository.RichiestaSupportoRepository;
import it.unicam.hackhub.domain.service.CalendarService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GRichiestaSupportoHandler {
    private final Sessione sessione;
    private final RichiestaSupportoRepository richiestaRepo;
    private final HackathonRepository hackathonRepo; 
    private final CalendarService calendarService;

    public GRichiestaSupportoHandler(Sessione sessione,
                                     RichiestaSupportoRepository richiestaRepo,
                                     HackathonRepository hackathonRepo,
                                     CalendarService calendarService) {
        this.sessione = sessione;
        this.richiestaRepo = richiestaRepo;
        this.hackathonRepo = hackathonRepo;
        this.calendarService = calendarService;
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
        rispondiRichiesta(richiesta, risposta);
        prenotaSlotCalendar(richiesta, data, ora);
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
