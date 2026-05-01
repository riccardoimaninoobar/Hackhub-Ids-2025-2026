package it.unicam.hackhub.domain.repository;

import it.unicam.hackhub.domain.model.SegnalazioneViolazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SegnalazioneRepository extends JpaRepository<SegnalazioneViolazione, Long> {

    // Query per trovare le segnalazioni APERTE relative agli hackathon creati da uno specifico organizzatore
    @Query("SELECT s FROM SegnalazioneViolazione s " +
            "WHERE s.stato = 'APERTA' " +
            "AND s.hackathon.organizzatore.username = :username")
    List<SegnalazioneViolazione> findAperteByOrganizzatore(@Param("username") String username);
}
