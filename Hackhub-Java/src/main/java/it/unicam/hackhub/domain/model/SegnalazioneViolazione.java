package it.unicam.hackhub.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entità di dominio che rappresenta una segnalazione di violazione
 * effettuata da un mentore verso un team all'interno di un hackathon.
 */
@Entity
@Table(name = "segnalazioni_violazione")
public class SegnalazioneViolazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relazione con l'utente (Mentore) che fa la segnalazione
    @ManyToOne(optional = false)
    @JoinColumn(name = "mentore_id", nullable = false, updatable = false)
    private Utente mentore;

    // Relazione con il Team accusato della violazione
    @ManyToOne(optional = false)
    @JoinColumn(name = "team_id", nullable = false, updatable = false)
    private Team teamSegnalato;

    // Relazione con l'Hackathon in cui è avvenuto il fatto
    @ManyToOne(optional = false)
    @JoinColumn(name = "hackathon_id", nullable = false, updatable = false)
    private Hackathon hackathon;

    @Column(nullable = false, length = 1000)
    private String descrizione;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoSegnalazione stato;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataSegnalazione;

    /**
     * Costruttore protetto richiesto obbligatoriamente da JPA/Hibernate.
     * Non deve essere usato per la logica di business.
     */
    protected SegnalazioneViolazione() {
    }

    /**
     * Costruttore di business per la creazione di una nuova segnalazione.
     * Garantisce che l'oggetto nasca sempre in uno stato valido.
     */
    public SegnalazioneViolazione(Utente mentore, Team teamSegnalato, Hackathon hackathon, String descrizione) {
        this.mentore = mentore;
        this.teamSegnalato = teamSegnalato;
        this.hackathon = hackathon;
        this.descrizione = descrizione;

        // Impostazioni automatiche alla creazione
        this.stato = StatoSegnalazione.APERTA;
        this.dataSegnalazione = LocalDateTime.now();
    }

    // --- Getters ---
    // (In DDD, limitiamo i setter solo a ciò che può effettivamente cambiare)

    public Long getId() { return id; }

    public Utente getMentore() { return mentore; }

    public Team getTeamSegnalato() { return teamSegnalato; }

    public Hackathon getHackathon() { return hackathon; }

    public String getDescrizione() { return descrizione; }

    public StatoSegnalazione getStato() { return stato; }

    public LocalDateTime getDataSegnalazione() { return dataSegnalazione; }

    // --- Metodi di Business per il cambio di stato ---


    public void accogli() {
        this.stato = StatoSegnalazione.ACCOLTA;
    }

    public void respingi() {
        this.stato = StatoSegnalazione.RESPINTA;
    }
}