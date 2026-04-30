package it.unicam.hackhub.domain.service;

public interface SistemaPagamentoAdapter {
    boolean erogaPagamento(double importo, String datiBancari);
}