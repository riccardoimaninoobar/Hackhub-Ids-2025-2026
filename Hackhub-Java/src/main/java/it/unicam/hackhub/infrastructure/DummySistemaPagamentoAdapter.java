package it.unicam.hackhub.infrastructure;

import it.unicam.hackhub.domain.service.SistemaPagamentoAdapter;

public class DummySistemaPagamentoAdapter implements SistemaPagamentoAdapter {

    @Override
    public boolean erogaPremio(double importo, String datiBancari) {
        System.out.println(">>> Contattando il Sistema di Pagamento Esterno...");
        
        // Simula il passo 3.a: Fallimento se mancano i dati bancari
        if (datiBancari == null || datiBancari.isEmpty() || datiBancari.equals("Dati non inseriti")) {
            System.out.println(">>> Errore di transazione: Dati bancari mancanti o non validi.");
            return false;
        }

        // Simula il passo 3 e 4: Successo della transazione
        System.out.println(">>> Transazione di " + importo + " EUR verso [" + datiBancari + "] autorizzata e completata.");
        return true;
    }
}