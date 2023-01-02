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
}
