package app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Stats {
    private static final int START_ELO = 100;
    private static final int START_WINS = 0;
    private static final int START_LOSSES = 0;

//    @JsonAlias({"id"})
//    private String id;

    @JsonAlias({"elo"})
    private int elo;

    @JsonAlias({"wins"})
    private int wins;

    @JsonAlias({"losses"})
    private int losses;

    public Stats() {
        this(START_ELO, START_WINS, START_LOSSES);
    }
}