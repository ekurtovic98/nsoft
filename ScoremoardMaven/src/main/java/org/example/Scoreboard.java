package org.example;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Scoreboard {
    private final Map<String, Match> matches = new LinkedHashMap<>();
    private final Lock lock = new ReentrantLock();
    private final ExecutorService executor = Executors.newCachedThreadPool();


    public void addMatch(Match match) {
            lock.lock();
            try {
                if (match==null) {
                    throw new IllegalArgumentException("Match cannot be null.");
                }

                String matchId = match.getHomeTeam() + " vs " + match.getAwayTeam();

                if (matches.containsKey(matchId)) {
                    throw new IllegalArgumentException("Match already in scoreboard: " + matchId);
                }
                matches.put(matchId, match);
                System.out.println("Added match: " + matchId + " to scoreboard");
            } finally {
                lock.unlock();
            }
    }


    public void getSummary() {
        lock.lock();
        try {
            System.out.println("Match summary:");
            matches.values().stream()
                    .filter(match -> !match.isFinished())
                    .sorted((m1, m2) -> {
                        int scoreComparison = Integer.compare(m2.getTotalScore(), m1.getTotalScore());
                        return (scoreComparison != 0) ? scoreComparison : Long.compare(m2.getStartTime(), m1.getStartTime());
                    })
                    .forEach(match -> {
                        System.out.println(String.format("Match %d: %s", matches.size(), match.getMatchSummary()));
                    });

        } finally {
            lock.unlock();
        }
    }


    public void updateMatchScore(String matchId, int homeScore, int awayScore) {
        lock.lock();
        try {
            if (!matches.containsKey(matchId)) {
                throw new IllegalArgumentException("Match not found: " + matchId);
            }
            Match match = matches.get(matchId);
            executor.execute(() -> match.updateScore(homeScore, awayScore));
        } finally {
            lock.unlock();
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
