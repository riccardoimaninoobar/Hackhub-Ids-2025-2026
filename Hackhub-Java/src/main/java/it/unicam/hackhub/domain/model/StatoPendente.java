package it.unicam.hackhub.domain.model;

public class StatoPendente implements StatoInvito {

    @Override
    public void accetta(Invito invito) {
        // 1. getTeam()
        Team team = invito.getTeam();

        // 2. aggiungiMembro(utente) -> che al suo interno farà setTeam(team) su Utente
        team.addMember(invito.getInvitato()); // Assumo tu abbia addMember o aggiungiMembro in Team

        // 3. <<create>> StatoAccettato e setState(accettato)
        invito.setStato(new StatoAccettato());
    }

    @Override
    public void rifiuta(Invito invito) {
        invito.setStato(new StatoRifiutato());
    }

    @Override
    public String getStato() { return "IN_ATTESA"; }
}