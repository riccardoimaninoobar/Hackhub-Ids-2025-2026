package it.unicam.hackhub.application.controller;

import java.time.LocalDate;
import java.time.LocalTime;

public interface CalendarService {
    boolean verificaDisponibilita(LocalDate data, LocalTime ora);
    boolean prenotaSlot(LocalDate data, LocalTime ora);
}
