package app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Stats {
    private static final int START_ELO = 1000;
    private static final int START_WINS = 0;
    private static final int START_LOSSES = 0;
    private static final int START_DRAWS = 0;

    @JsonIgnore
    @JsonAlias({"id"})
    private String id = UUID.randomUUID().toString();

    @JsonAlias({"elo"})
    private int elo;

    @JsonAlias({"wins"})
    private int wins;

    @JsonAlias({"losses"})
    private int losses;

    @JsonAlias({"draws"})
    private int draws;

    @JsonIgnore
    @JsonAlias({"user_id"})
    private String userId;

    public Stats() {
        this(START_ELO, START_WINS, START_LOSSES, START_DRAWS, null);
    }

    public Stats(String userId) {
        this(START_ELO, START_WINS, START_LOSSES, START_DRAWS, userId);
    }

    public Stats(int elo, int wins, int losses, int draws, String userId) {
        setElo(elo);
        setWins(wins);
        setLosses(losses);
        setDraws(draws);
        setUserId(userId);
    }
}