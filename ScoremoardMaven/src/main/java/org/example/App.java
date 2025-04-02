package org.example;

public class App {
    public static void main(String[] args) throws InterruptedException {
        // Kreiranje novog scoreboard-a
        Scoreboard scoreboard = new Scoreboard();

        // Kreiranje nekoliko utakmica
        Match match1 = new Match("Mexico", "Canada");
        Match match2 = new Match("Spain", "Brazil");
        Match match3 = new Match("Germany", "France");
        Match match4 = new Match("Uruguay", "Italy");
        Match match5 = new Match("Argentina", "Australia");

        // Dodavanje utakmica na scoreboard
        scoreboard.addMatch(match1);
        scoreboard.addMatch(match2);
        scoreboard.addMatch(match3);
        scoreboard.addMatch(match4);
        scoreboard.addMatch(match5);

        // Početak utakmica
        match1.startMatch();
        match2.startMatch();
        match3.startMatch();
        match4.startMatch();
        match5.startMatch();



        // Ažuriranje rezultata za sve utakmice
        match1.updateScore(0, 5);  // Mexico 0 - Canada 5
        match2.updateScore(10, 2); // Spain 10 - Brazil 2
        match3.updateScore(2, 2);  // Germany 2 - France 2
        match4.updateScore(6, 6);  // Uruguay 6 - Italy 6
        match5.updateScore(3, 1);  // Argentina 3 - Australia 1
        // Ispis rezultata svih utakmica
        Thread.sleep(1000);  // 1000 milisekundi = 1 sekunda

        scoreboard.getSummary();

        // Završavanje utakmica
        match1.finishMatch();
        match2.finishMatch();
        match3.finishMatch();
        match4.finishMatch();
        match5.finishMatch();

        // Dodavanje kašnjenja prije ispisivanja rezultata
        Thread.sleep(1000);  // 1000 milisekundi = 1 sekunda

        // Ispis rezultata svih utakmica
        scoreboard.getSummary();
    }
}
