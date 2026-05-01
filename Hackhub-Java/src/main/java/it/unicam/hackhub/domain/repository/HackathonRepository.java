package it.unicam.hackhub.domain.repository;

import it.unicam.hackhub.domain.model.hackathon.Hackathon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface HackathonRepository extends JpaRepository<Hackathon, Long> {
        Optional<Hackathon> findByNome(String nome);
        boolean existsByNome(String nome);
        @Query("SELECT h FROM Hackathon h JOIN h.mentori m WHERE m.username = :username AND h.stato != 'CONCLUSO'")
        List<Hackathon> findAttiviByMentore(@Param("username") String username);
}
