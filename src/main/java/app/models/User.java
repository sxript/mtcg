package app.models;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
    private static final int START_COINS = 20;

    @JsonAlias({"id"})
    private String id = UUID.randomUUID().toString();

    @JsonAlias({"Name"})
    private String name;

    @JsonAlias({"Username"})
    private String username;

    @JsonAlias({"Password"})
    private String password;

    @JsonAlias({"Coins"})
    @JsonSetter(nulls = Nulls.SKIP)
    private int coins = START_COINS;

    private boolean isAdmin = false;

    public User(String id, String name, String username, String password, Integer coins) {
        setId(id);
        setName(name);
        setUsername(username);
        setPassword(password);
        setCoins(coins);
    }

    public User(String name, String username, String password, Integer coins) {
        setName(name);
        setUsername(username);
        setPassword(password);
        setCoins(coins);

    }

    public User(String username, String password) {
        this(null, username, password, START_COINS);
    }
}
