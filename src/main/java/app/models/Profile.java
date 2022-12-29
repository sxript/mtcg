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
public class Profile {
    // TODO: ADD ID
    @JsonAlias({"Bio"})
    private String bio;

    @JsonAlias({"Image"})
    private String image;

    public Profile () {}
}
