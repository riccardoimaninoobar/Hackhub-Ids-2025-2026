package it.unicam.hackhub.domain.service;

public interface SistemaPagamentoAdapter {
    boolean erogaPremio(double importo, String datiBancari);
}