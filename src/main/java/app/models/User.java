package app.models;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.util.List;
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

    @JsonAlias({"Stack"})
    private List<String> stack;

    @JsonAlias({"Deck"})
    private Deck deck;

    // TODO: DELETE STATS AND PROFILE THESE DO NOT BELONG HERE
    @JsonAlias({"Stats"})
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonUnwrapped
    private Stats stats = new Stats();

    @JsonAlias({"Profile"})
    @JsonUnwrapped
    private Profile profile;

    private boolean isAdmin = false;

    public User(String id, String name, String username, String password, Integer coins, Stats stats, Profile profile) {
        setId(id);
        setName(name);
        setUsername(username);
        setPassword(password);
        setCoins(coins);
        setStats(stats);
        setProfile(profile);

    }
    public User(String name, String username, String password, Integer coins, Stats stats, Profile profile) {
        setName(name);
        setUsername(username);
        setPassword(password);
        setCoins(coins);
        setStats(stats);
        setProfile(profile);

    }
    public User(String username, String password) {
        this(null, username, password, START_COINS,  new Stats(), new Profile());
    }

    //    public Card drawCard() {
//        return deck.getDeck().get(rnd.nextInt(deck.getDeck().size()));
//    }
//
//    public boolean removeCard(Card card) {
//        return deck.getDeck().remove(card);
//    }
//
//    public void addCard(Card card) {
//        deck.getDeck().add(card);
//    }
}
