package it.unicam.hackhub.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "notifiche")
public class Notifica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinatario_id", nullable = false)
    private final Utente destinatario;

    @Column(nullable = false)
    private final String titolo;

    @Column(nullable = false, length = 1000)
    private final String messaggio;

    protected Notifica() {
        this.destinatario = null;
        this.titolo = null;
        this.messaggio = null;
    }

    public Notifica(Utente destinatario, String titolo, String messaggio) {
        this.destinatario = destinatario;
        this.titolo = titolo;
        this.messaggio = messaggio;
    }

    public Utente getDestinatario() {
        return destinatario;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getMessaggio() {
        return messaggio;
    }
}