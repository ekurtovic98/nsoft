package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

class ScoreboardTest {
    private Scoreboard scoreboard;
    private Match match1;
    private Match match2;
    private ExecutorService executor;
    private ByteArrayOutputStream outputStreamCaptor;

    @BeforeEach
    void setUp() {
        scoreboard = new Scoreboard();
        match1 = new Match("Team A", "Team B");
        match2 = new Match("Team C", "Team D");
        executor = Executors.newSingleThreadExecutor();
        outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor)); // Redirect System.out to capture output
    }

    @Test
    void testAddMatch() {
        scoreboard.addMatch(match1);

        await().atMost(1, SECONDS).untilAsserted(() -> {
            scoreboard.getSummary();

            String expectedSummary = "Team A vs Team B";
            assertTrue(outputStreamCaptor.toString().contains(expectedSummary));
        });
    }


    @Test
    void testAddMatchWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            scoreboard.addMatch(null);
        }, "Match cannot be null.");
    }


    @Test
    void testAddDuplicateMatch() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        scoreboard.addMatch(match1);

        executor.submit(() -> {
            try {
                scoreboard.addMatch(match1);
                fail("Expected IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                latch.countDown();
            }
        });

        latch.await(1, SECONDS);
    }


    @Test
    void testGetSummary() {
        scoreboard.addMatch(match1);
        scoreboard.addMatch(match2);

        await().atMost(1, SECONDS).untilAsserted(() -> {
            scoreboard.getSummary();
            String summary = outputStreamCaptor.toString();
            assertTrue(summary.contains("Match summary:"));
            assertTrue(summary.contains("Team A vs Team B"));
            assertTrue(summary.contains("Team C vs Team D"));
        });
    }

    @Test
    void testMatchIsNotFinishedInSummary() {
        match1.startMatch();
        scoreboard.addMatch(match1);

        await().atMost(1, SECONDS).untilAsserted(() -> {
            // Capture the summary output
            scoreboard.getSummary();
            String summary = outputStreamCaptor.toString();
            assertTrue(summary.contains("Team A vs Team B"));
        });
    }

    @Test
    public void testUpdateMatchScore() {
        Scoreboard scoreboard = new Scoreboard();
        Match match = new Match("Team A", "Team B");
        scoreboard.addMatch(match);

        scoreboard.updateMatchScore("Team A vs Team B", 2, 3);

        await().atMost(1, TimeUnit.SECONDS).until(() ->
                match.getHomeScore() == 2 && match.getAwayScore() == 3
        );

        assertEquals(2, match.getHomeScore());
        assertEquals(3, match.getAwayScore());
    }

}
