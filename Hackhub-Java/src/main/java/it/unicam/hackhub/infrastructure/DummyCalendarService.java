package it.unicam.hackhub.infrastructure;

import it.unicam.hackhub.domain.service.CalendarService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class DummyCalendarService implements CalendarService {
    private final Set<String> slotPrenotati = new HashSet<>();

    @Override
    public boolean verificaDisponibilita(LocalDate data, LocalTime ora) {
        return !slotPrenotati.contains(key(data, ora));
    }

    @Override
    public boolean prenotaSlot(LocalDate data, LocalTime ora) {
        return slotPrenotati.add(key(data, ora));
    }

    private String key(LocalDate data, LocalTime ora) {
        return data + "@" + ora;
    }
}
