package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.domain.model.Hackathon;
import it.unicam.hackhub.domain.repository.HackathonRepository;

import java.util.List;

public class ConsultareHackathonHandler {

    private final HackathonRepository hackathonRepo;

    public ConsultareHackathonHandler(HackathonRepository hackathonRepo) {
        this.hackathonRepo = hackathonRepo;
    }

    /**
     * Restituisce la lista di tutti gli Hackathon.
     */
    public List<Hackathon> getListaHackathon() {
        return hackathonRepo.findAll();
    }
}