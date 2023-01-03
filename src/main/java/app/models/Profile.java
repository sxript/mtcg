package app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Profile {
    @JsonIgnore
    @JsonAlias({"id"})
    private String id = UUID.randomUUID().toString();

    @JsonAlias({"Bio"})
    private String bio;

    @JsonAlias({"Image"})
    private String image;

    @JsonIgnore
    @JsonAlias({"user_id"})
    private String userId;
}
