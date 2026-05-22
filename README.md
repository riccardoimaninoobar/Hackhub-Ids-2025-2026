# Hackhub-Ids-2025-2026
Repository del progetto **HackHub**, sviluppato per l'esame di **Ingegneria Del Software** (A.A. 2025-2026).

##  Descrizione del Progetto
HackHub è una piattaforma software sviluppata in **Java (Spring Boot)** dedicata alla gestione completa di Hackathon. L'applicazione permette agli organizzatori di creare eventi, ai partecipanti di formare team e competere, e a giudici/mentori di supportare e valutare le sottomissioni.

## Funzionalità Principali
- **Gestione Utenti e Ruoli:** Accesso profilato per Organizzatori, Giudici, Mentori e Partecipanti.
- **Gestione Team:** Creazione di team e sistema di inviti interattivo tra utenti.
- **Ciclo di Vita dell'Hackathon:** Gestione dinamica degli stati dell'evento (In Iscrizione, In Corso, In Valutazione, Concluso) implementata tramite lo *State Pattern*.
- **Sottomissioni e Valutazioni:** Caricamento dei file di progetto da parte dei team e assegnazione dei punteggi da parte dei giudici.
- **Richieste di Supporto (Mentoring):** Sistema per richiedere aiuto ai mentori, con prenotazione integrata di slot su un calendario dedicato.
- **Sistema di Notifiche Event-Driven:** Architettura reattiva per le notifiche (es. gestione del supporto, segnalazioni, inviti) sviluppata sfruttando gli *Application Events* di Spring.
- **Segnalazione Violazioni:** Monitoraggio e segnalazione di comportamenti che infrangono il regolamento.

## Tecnologie e Pattern Utilizzati
- **Linguaggio:** Java
- **Framework:** Spring Boot (Core, Data JPA, Events)
- **Persistenza:** JPA / Hibernate
- **Architettura:** Layered Architecture orientata al dominio (Domain, Application, Infrastructure).
- **Testing:** JUnit 5, Mockito.
- **Design Pattern:** State Pattern, Observer/Pub-Sub (Tramite Spring Event Publisher), Builder.

## Avvio Rapido
Il progetto è provvisto di un `DatabaseSeeder` che, all'avvio in ambiente di sviluppo, popola automaticamente il database con dati mock utili per testare subito i vari casi d'uso (Hackathon nei diversi stati, team preconfigurati, sottomissioni già effettuate).

Per avviare l'applicazione localmente:
- [x] **Clona il repository** sul tuo ambiente locale.
- [x] **Importa il progetto** nel tuo IDE.
- [x] **Sincronizza le dipendenze** (Assicurati che le dipendenze siano state scaricate correttamente).
- [x] **Esegui la classe principale** di Spring Boot per avviare il server.

### Dati di test precaricati
Potrai utilizzare i seguenti account di test per esplorare le funzionalità:
- **Organizzatore:** `org@hack.it` (pass: `pass123`)
- **Giudice:** `giudice@hack.it` (pass: `pass123`)
- **Mentore:** `mentore@hack.it` (pass: `pass123`)
- **Leader Team A:** `leada@hack.it` (pass: `pass123`)

## Esplorare e Testare le API
L'interazione con il sistema backend avviene interamente tramite chiamate REST. Puoi esplorare gli endpoint in due modi:

- 🟢 **Swagger UI:** La dashboard interattiva auto-generata. A server avviato, visita 👉 `http://localhost:8080/swagger-ui.html` per visualizzare e testare direttamente tutti i vari casi d'uso.
- 🟠 **Postman:** Punta l'URL base a `http://localhost:8080` e componi manualmente le tue richieste (`GET`, `POST`, `PUT`, `DELETE`) sfruttando i dati mockati per sperimentare i flussi.