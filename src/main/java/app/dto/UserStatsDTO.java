package app.dto;

import app.models.Stats;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatsDTO {
    private String username;
    private String name;
    private Stats stats;
    private double winLoseRatio;

    public void setStats(Stats stats) {
        this.winLoseRatio = stats.getLosses() == 0 ? stats.getWins() : (double)stats.getWins() / (double)stats.getLosses();
        this.stats = stats;
    }
}
