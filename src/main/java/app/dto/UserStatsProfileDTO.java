package app.dto;

import app.models.Profile;
import app.models.Stats;
import app.models.User;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserStatsProfileDTO {
    @JsonAlias({"User"})
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonUnwrapped
    private User user;

    @JsonAlias({"Profile"})
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonUnwrapped
    private Profile profile;

    @JsonAlias({"Stats"})
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonUnwrapped
    private Stats stats;
}
