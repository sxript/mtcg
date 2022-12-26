package game;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Stats {
    private static final int START_ELO = 100;
    private static final int START_WINS = 0;
    private static final int START_LOSSES = 0;
    private int elo;
    private int wins;
    private int losses;

    public Stats(int elo, int wins, int losses) {
        setElo(elo);
        setWins(wins);
        setLosses(losses);
    }

    public Stats() {
        this(START_ELO, START_WINS, START_LOSSES);
    }

    @Override
    public String toString() {
        return "Stats{" +
                "elo=" + elo +
                ", wins=" + wins +
                ", losses=" + losses +
                '}';
    }
}
