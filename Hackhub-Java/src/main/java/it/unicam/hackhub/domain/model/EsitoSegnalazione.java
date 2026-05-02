package it.unicam.hackhub.domain.model;

/**
 * Rappresenta i possibili stati di una Segnalazione di Violazione.
 */
public enum EsitoSegnalazione {
    APERTA,          // Appena creata dal mentore
    ACCOLTA,         // Violazione confermata (es. team squalificato)
    RESPINTA         // Violazione non sussistente
}