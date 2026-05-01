package it.unicam.hackhub.infrastructure.persistence;

import it.unicam.hackhub.domain.model.hackathon.state.*;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatoHackathonConverter implements AttributeConverter<StatoHackathon, String> {

    // Da Java a Database (Quando fai save)
    @Override
    public String convertToDatabaseColumn(StatoHackathon stato) {
        if (stato == null || stato instanceof StatoInCreazione) return "IN_CREAZIONE";
        if (stato instanceof StatoInIscrizione) return "IN_ISCRIZIONE";
        if (stato instanceof StatoInCorso) return "IN_CORSO";
        if (stato instanceof StatoInValutazione) return "IN_VALUTAZIONE";
        if (stato instanceof StatoConcluso) return "CONCLUSO";
        return "IN_ISCRIZIONE";
    }

    // Da Database a Java (Quando fai findAll)
    @Override
    public StatoHackathon convertToEntityAttribute(String dbData) {
        if (dbData == null) return new StatoInIscrizione();
        return switch (dbData) {
            case "IN_CORSO" -> new StatoInCorso();
            case "IN_VALUTAZIONE" -> new StatoInValutazione();
            case "CONCLUSO" -> new StatoConcluso();
            default -> new StatoInIscrizione();
        };
    }
}