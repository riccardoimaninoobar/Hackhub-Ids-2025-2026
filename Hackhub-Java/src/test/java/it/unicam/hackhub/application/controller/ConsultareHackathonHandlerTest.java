package it.unicam.hackhub.application.controller;

import it.unicam.hackhub.domain.model.Hackathon;
import it.unicam.hackhub.domain.model.HackathonBuilder;
import it.unicam.hackhub.domain.model.Utente;
import it.unicam.hackhub.domain.repository.HackathonRepository;
import it.unicam.hackhub.infrastructure.persistence.InMemoryHackathonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConsultareHackathonHandlerTest {

    private HackathonRepository hackathonRepository;
    private ConsultareHackathonHandler handler;

    @BeforeEach
    void setUp() {
        hackathonRepository = new InMemoryHackathonRepository();
        handler = new ConsultareHackathonHandler(hackathonRepository);
    }

    @Test
    void getListaHackathon_restituisceListaVuota_quandoNonCiSonoHackathon() {
        List<Hackathon> risultato = handler.getListaHackathon();

        assertNotNull(risultato);
        assertTrue(risultato.isEmpty());
    }

    @Test
    void getListaHackathon_restituisceGliHackathonPresentiNelRepository() {
        Utente organizzatore = new Utente("org1", "org1@mail.it", "pass");
        Utente giudice = new Utente("giudice1", "giudice1@mail.it", "pass");
        Utente mentore = new Utente("mentore1", "mentore1@mail.it", "pass");

        Hackathon hackathon1 = new HackathonBuilder()
                .assegnaNome("Hackathon AI")
                .assegnaRegolamento("Regolamento AI")
                .assegnaScadenza(LocalDate.now().plusDays(10))
                .assegnaDataInizio(LocalDate.now().plusDays(20))
                .assegnaDataFine(LocalDate.now().plusDays(30))
                .assegnaLuogo("Camerino")
                .assegnaDimMaxTeam(5)
                .assegnaPremioImporto(new BigDecimal("1000"))
                .assegnaOrganizzatore(organizzatore)
                .assegnaGiudice(giudice)
                .assegnaMentore(mentore)
                .build();

        Hackathon hackathon2 = new HackathonBuilder()
                .assegnaNome("Hackathon Web")
                .assegnaRegolamento("Regolamento Web")
                .assegnaScadenza(LocalDate.now().plusDays(15))
                .assegnaDataInizio(LocalDate.now().plusDays(25))
                .assegnaDataFine(LocalDate.now().plusDays(35))
                .assegnaLuogo("Ancona")
                .assegnaDimMaxTeam(4)
                .assegnaPremioImporto(new BigDecimal("2000"))
                .assegnaOrganizzatore(organizzatore)
                .assegnaGiudice(giudice)
                .assegnaMentore(mentore)
                .build();

        hackathonRepository.save(hackathon1);
        hackathonRepository.save(hackathon2);

        List<Hackathon> risultato = handler.getListaHackathon();

        assertNotNull(risultato);
        assertEquals(2, risultato.size());
        assertTrue(risultato.stream().anyMatch(h -> h.getNome().equals("Hackathon AI")));
        assertTrue(risultato.stream().anyMatch(h -> h.getNome().equals("Hackathon Web")));
    }
}
