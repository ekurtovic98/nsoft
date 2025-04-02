package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.awaitility.Awaitility.await;

class MatchTest {
    private Match match;
    private ExecutorService executor;

    @BeforeEach
    void setUp() {
        match = new Match("Team A", "Team B");
        executor = Executors.newSingleThreadExecutor();
    }

    @Test
    void testInitialMatchState() {
        assertEquals("Team A", match.getHomeTeam());
        assertEquals("Team B", match.getAwayTeam());
        assertEquals(0, match.getHomeScore());
        assertEquals(0, match.getAwayScore());
        assertFalse(match.isFinished());
        assertEquals(-1, match.getStartTime());
    }

    @Test
    void testUpdateScore() {
        match.updateScore(2, 3);

        await().atMost(1, SECONDS).untilAsserted(() -> {
            assertEquals(2, match.getHomeScore());
            assertEquals(3, match.getAwayScore());
        });
    }


    @Test
    void testUpdateScoreWithNegativeValues() {
        assertThrows(IllegalArgumentException.class, () -> match.updateScore(-1, 2), "Scores must be non-negative.");
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThrows(IllegalArgumentException.class, () -> match.updateScore(-1, 2), "Scores must be non-negative.");
        });
    }

    @Test
    void testFinishMatch() {
        match.finishMatch();

        await().atMost(1, SECONDS).untilAsserted(() -> {
            assertTrue(match.isFinished());
        });
    }


    @Test
    void testCannotUpdateScoreAfterFinish() {
        match.finishMatch();


    }

    @Test
    void testConcurrentScoreUpdate() {
        executor.submit(() -> match.updateScore(1, 0));
        executor.submit(() -> match.updateScore(2, 1));

        await().atMost(2, SECONDS).untilAsserted(() -> {
            assertTrue(match.getHomeScore() >= 0);
            assertTrue(match.getAwayScore() >= 0);
        });
    }

    @Test
    void testConcurrentMatchFinish() {
        match.finishMatch();

        await().atMost(2, SECONDS).untilAsserted(() -> {
            assertTrue(match.isFinished());
        });
    }

    @Test
    void testCannotStartFinishedMatch() {
        match.finishMatch();

        await().atMost(1, SECONDS).untilAsserted(() -> {
            IllegalStateException exception = assertThrows(IllegalStateException.class, match::startMatch);
            assertEquals("Cannot start a finished match.", exception.getMessage());
        });
    }

    @Test
    void testStartMatch() {
        match.startMatch();

        await().atMost(1, SECONDS).untilAsserted(() -> {
            assertTrue(match.getStartTime() > 0);
        });
    }

}
