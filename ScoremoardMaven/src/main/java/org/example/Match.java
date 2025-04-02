package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Match {
    private final String homeTeam;
    private final String awayTeam;
    private int homeScore;
    private int awayScore;
    private boolean finished;
    private long startTime;

    private final Lock lock = new ReentrantLock();
    private final Condition operationFinishedCondition = lock.newCondition();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public Match(String homeTeam, String awayTeam) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = 0;
        this.awayScore = 0;
        this.finished = false;
        this.startTime = -1;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public int getHomeScore() {
        lock.lock();
        try {
            return homeScore;
        } finally {
            lock.unlock();
        }
    }

    public void setHomeScore(int homeScore) {
        lock.lock();
        try {
            if (homeScore < 0) {
                throw new IllegalArgumentException("Score must be non-negative.");
            }
            if (finished) {
                throw new IllegalStateException("Cannot set score for a finished match.");
            }

            this.homeScore = homeScore;
        } finally {
            lock.unlock();
        }
    }

    public void setAwayScore(int awayScore) {
        lock.lock();
        try {
            if (awayScore < 0) {
                throw new IllegalArgumentException("Score must be non-negative.");
            }
            if (finished) {
                throw new IllegalStateException("Cannot set score for a finished match.");
            }

            this.awayScore = awayScore;
        } finally {
            lock.unlock();
        }
    }

    public void startMatch() {
        lock.lock();
        try {
            setStartTime(System.currentTimeMillis());
            System.out.println("Match started: " + homeTeam + " vs " + awayTeam);
        } finally {
            lock.unlock();
        }
    }

    public int getAwayScore() {
        lock.lock();
        try {
            return awayScore;
        } finally {
            lock.unlock();
        }
    }

    public boolean isFinished() {
        lock.lock();
        try {
            return finished;
        } finally {
            lock.unlock();
        }
    }

    public long getStartTime() {
        lock.lock();
        try {
            return startTime;
        } finally {
            lock.unlock();
        }
    }

    public int getTotalScore() {
        lock.lock();
        try {
            return homeScore + awayScore;
        } finally {
            lock.unlock();
        }
    }

    public String getMatchSummary() {
        lock.lock();
        try {
            return homeTeam + " " + homeScore + " - " + awayScore + " " + awayTeam;
        } finally {
            lock.unlock();
        }
    }

    public void updateScore(int homeScore, int awayScore) {
        lock.lock();
        try {
            if (this.finished) {
                throw new IllegalStateException("Cannot update score for a finished match.");
            }
            if (homeScore < 0 || awayScore < 0) {
                throw new IllegalArgumentException("Scores cannot be negative.");
            }
            if (homeScore < this.homeScore || awayScore < this.awayScore) {
                throw new IllegalArgumentException("Scores cannot decrease.");
            }

            this.homeScore = homeScore;
            this.awayScore = awayScore;

        } finally {
            lock.unlock();
        }
    }



    public void finishMatch() {
        executor.execute(() -> {
            lock.lock();
            try {
                if (finished) {
                    throw new IllegalStateException("Match has already finished.");
                }
                this.finished = true;
                operationFinishedCondition.signalAll();
                System.out.println("Match finished: " + getMatchSummary());
            } finally {
                lock.unlock();
            }
        });
    }

    public void setStartTime(long startTime) {
        lock.lock();
        try {
            if (finished) {
                throw new IllegalStateException("Cannot start a finished match.");
            }
            if (this.startTime != -1) {
                throw new IllegalStateException("Match has already started.");
            }
            this.startTime = startTime;
        } finally {
            lock.unlock();
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
