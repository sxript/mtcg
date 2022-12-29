package app.models;


import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Deck {
    public static final int DECK_SIZE = 4;

    @JsonAlias({"Id"})
    private String id = UUID.randomUUID().toString();

    @JsonAlias({"user_id"})
    private String userId;

    public Deck(String userId) {
        setUserId(userId);
    }
}
