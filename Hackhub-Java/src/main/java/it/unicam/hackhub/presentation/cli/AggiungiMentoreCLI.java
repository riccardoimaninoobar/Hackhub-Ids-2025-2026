package it.unicam.hackhub.presentation.cli;

import it.unicam.hackhub.application.controller.AggiungiMentoreHandler;
import it.unicam.hackhub.domain.model.Utente;

import java.util.Scanner;

public class AggiungiMentoreCLI {
     private final AggiungiMentoreHandler handler;
     private final Scanner scanner = new Scanner(System.in);

     public AggiungiMentoreCLI(AggiungiMentoreHandler handler) {
         this.handler = handler;
     }


     public void run(Utente currentUtente) {
          System.out.println("Inserisci nome dell'hackathon:");
          String nomeHackathon = scanner.nextLine();
          richiediInserimentoMentore(currentUtente, nomeHackathon);
         boolean ok = false;
         while (!ok) {
             System.out.println("Inserire username per mentore");
             String username = scanner.nextLine();
             try {
                 inserisciMentore(username);
                 ok = true;
             } catch (IllegalArgumentException e) {
                 System.out.println(e.getMessage());
             }
         }
     }

     public void richiediInserimentoMentore(Utente utente, String nomeHackathon) {
         try {
             handler.checkOrg(utente, nomeHackathon);
         } catch (Exception e) {
             System.out.println(e.getMessage());
         }
     }
     public void inserisciMentore(String username){
         handler.aggiungiMentore(username);

     }

}
