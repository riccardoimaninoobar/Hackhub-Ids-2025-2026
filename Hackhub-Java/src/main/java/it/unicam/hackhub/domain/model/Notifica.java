package it.unicam.hackhub.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifiche")
public class Notifica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "destinatario_id", nullable = false)
    private Utente destinatario;

    @Column(nullable = false)
    private String titolo;

    @Column(nullable = false, length = 1000)
    private String messaggio;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCreazione;

    protected Notifica() {}

    public Notifica(Utente destinatario, String titolo, String messaggio) {
        this.destinatario = destinatario;
        this.titolo = titolo;
        this.messaggio = messaggio;
        this.dataCreazione = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Utente getDestinatario() { return destinatario; }
    public String getTitolo() { return titolo; }
    public String getMessaggio() { return messaggio; }
    public LocalDateTime getDataCreazione() { return dataCreazione; }
}