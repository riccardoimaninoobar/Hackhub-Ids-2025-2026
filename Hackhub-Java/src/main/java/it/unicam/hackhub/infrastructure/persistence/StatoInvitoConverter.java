package it.unicam.hackhub.infrastructure.persistence;

import it.unicam.hackhub.domain.model.*;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatoInvitoConverter implements AttributeConverter<StatoInvito, String> {

    @Override
    public String convertToDatabaseColumn(StatoInvito stato) {
        if (stato == null) return "PENDENTE";
        if (stato instanceof StatoPendente) return "PENDENTE";
        if (stato instanceof StatoAccettato) return "ACCETTATO";
        if (stato instanceof StatoRifiutato) return "RIFIUTATO";
        return "PENDENTE";
    }

    @Override
    public StatoInvito convertToEntityAttribute(String dbData) {
        if (dbData == null) return new StatoPendente();
        return switch (dbData) {
            case "ACCETTATO" -> new StatoAccettato();
            case "RIFIUTATO" -> new StatoRifiutato();
            default -> new StatoPendente();
        };
    }
}