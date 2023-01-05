package game;

import app.models.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EloRaterTest {
    private EloRater eloRater;
    private Stats statsA;
    private Stats statsB;

    @BeforeEach
    void init() {
        eloRater = new EloRater();

        // Both players played more than 30 Games so k-Factor is 20 for both
        statsA = new Stats(2306, 10, 10, 11, null);
        statsB = new Stats(2077, 14, 20, 1, null);
    }

    @Test
    void win_updates_stats() {
        // StatsA Player won against StatsB Player
        eloRater.calculateRating(statsA, statsB, false);

        // Numbers from https://de.wikipedia.org/wiki/Elo-Zahl
        assertEquals(2310, statsA.getElo());
        assertEquals(2073, statsB.getElo());

        // Make sure that Game history updates
        assertEquals(11, statsA.getWins());
        assertEquals(21, statsB.getLosses());
    }

    @Test
    void lose_updates_stats() {
        // StatsA Player lost against StatsB Player
        eloRater.calculateRating(statsB, statsA, false);

        // Numbers from https://de.wikipedia.org/wiki/Elo-Zahl
        assertEquals(2290, statsA.getElo());
        assertEquals(2093, statsB.getElo());

        // Make sure that Game history updates
        assertEquals(11, statsA.getLosses());
        assertEquals(15, statsB.getWins());
    }

    @Test
    void draw_updates_stats() {
        // StatsA Player played draw against StatsB Player
        eloRater.calculateRating(statsB, statsA, true);

        // Numbers from https://de.wikipedia.org/wiki/Elo-Zahl
        assertEquals(2300, statsA.getElo());
        assertEquals(2083, statsB.getElo());

        // Make sure that Game history updates
        assertEquals(12, statsA.getDraws());
        assertEquals(2, statsB.getDraws());
    }
}